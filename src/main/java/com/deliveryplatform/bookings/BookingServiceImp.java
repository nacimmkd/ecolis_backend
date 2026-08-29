package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.CreateBookingRequest;
import com.deliveryplatform.bookings.events.BookingCanceledEvent;
import com.deliveryplatform.bookings.events.BookingCompletedEvent;
import com.deliveryplatform.bookings.events.BookingAcceptedEvent;
import com.deliveryplatform.bookings.events.BookingRequestEvent;
import com.deliveryplatform.bookings.exceptions.BookingErrorCode;
import com.deliveryplatform.bookings.exceptions.BookingException;
import com.deliveryplatform.matching.DetourCalculatorService;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.exceptions.ParcelErrorCode;
import com.deliveryplatform.parcels.exceptions.ParcelException;
import com.deliveryplatform.payments.PaymentService;
import com.deliveryplatform.payments.PriceCalculator;
import com.deliveryplatform.trips.TripRepository;
import com.deliveryplatform.trips.events.TripCompletedEvent;
import com.deliveryplatform.trips.exceptions.TripErrorCode;
import com.deliveryplatform.trips.exceptions.TripException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingRepository         bookingRepository;
    private final ParcelRepository          parcelRepository;
    private final TripRepository            tripRepository;
    private final DetourCalculatorService   detourCalculator;
    private final BookingMapper             bookingMapper;
    private final PaymentService            paymentService;
    private final PriceCalculator           priceCalculator;
    private final ApplicationEventPublisher eventPublisher;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public BookingDto getBooking(UUID bookingId, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);
        booking.assertUserInvolved(currentUserId);
        return bookingMapper.toDto(booking);
    }

    @Override
    public Page<BookingDto> getSentBookings(UUID currentUserId, Pageable pageable) {
        return bookingRepository.findSentBookingsByUserId(currentUserId, pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    public Page<BookingDto> getReceivedBookings(UUID currentUserId, Pageable pageable) {
        return bookingRepository.findReceivedBookingsByUserId(currentUserId, pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    @Transactional
    public BookingDto getOrCreateBooking(CreateBookingRequest dto, UUID senderId) {
        var parcel = parcelRepository.findParcelSummaryById(dto.parcelId())
                .orElseThrow(() -> new ParcelException(ParcelErrorCode.PARCEL_NOT_FOUND, "Parcel not found"));

        parcel.assertOwnedBy(senderId);

        var existingBooking = bookingRepository.findByTripIdAndParcelIdAndStateIn(
                dto.tripId(), dto.parcelId(), List.of(BookingState.PENDING, BookingState.ACCEPTED));
        if (existingBooking.isPresent()) {
            return bookingMapper.toDto(existingBooking.get());
        }

        var trip = tripRepository.findTripById(dto.tripId())
                .orElseThrow(() -> new TripException(TripErrorCode.TRIP_NOT_OWNED, "Trip not found"));

        var detour = detourCalculator.calculate(trip, parcel);
        var price = priceCalculator.calculateBookingPrice(trip, parcel);
        var booking = Booking.create(trip, parcel, detour, price);
        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }


    @Override
    @Transactional
    public void requestDriver(UUID bookingId, UUID userId) {
        var booking = getBookingByIdOrThrow(bookingId);

        if (!paymentService.isAuthorized(bookingId)) {
            throw new BookingException(BookingErrorCode.PAYMENT_REQUIRED,
                    "Booking " + bookingId + " must be paid before it can be sent to the carrier");
        }
        booking.sendRequest();

        var trip = booking.getTrip();
        if (trip.isInstantBooking()) {
            paymentService.capture(bookingId);
            booking.accept(userId);
        }

        eventPublisher.publishEvent(new BookingRequestEvent(
                booking.getId(),
                booking.getCarrier(),
                trip.getDepartureAddress().getCity(),
                trip.getArrivalAddress().getCity()
        ));
    }

    @Override
    @Transactional
    public void acceptBooking(UUID bookingId, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);

        paymentService.capture(bookingId);
        booking.accept(currentUserId);

        bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingAcceptedEvent(booking.getId(), booking.getSender()));
    }

    @Override
    @Transactional
    public void rejectBooking(UUID bookingId, UUID carrierId, String reason) {
        var booking = getBookingByIdOrThrow(bookingId);
        booking.assertIsCarrier(carrierId);

        paymentService.cancel(bookingId);
        booking.reject(carrierId, reason);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancel(UUID bookingId, String reason, UUID currentUserId) {

        var booking = getBookingByIdOrThrow(bookingId);

        var cancelledBy = booking.resolveCanceller(currentUserId);
        booking.cancel(currentUserId, reason, cancelledBy);
        paymentService.cancel(bookingId);

        eventPublisher.publishEvent(new BookingCanceledEvent(booking.getId(), booking.getSender()));
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void confirmPickUp(UUID bookingId, String pickUpCode, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);
        booking.confirmPickUp(pickUpCode);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void complete(UUID bookingId, String dropOfCode, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);

        var trip = booking.getTrip();
        trip.assertOwnedBy(currentUserId);

        booking.complete(dropOfCode);

        // complete trip if all bookings are delivered
        if (isAllTripBookingsCompleted(trip.getId())) {
            trip.complete();
            eventPublisher.publishEvent(new TripCompletedEvent(trip.getId(), trip.getOwner().getId()));
        }

        bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingCompletedEvent(booking.getId(), booking.getSender()));
    }

    // PRIVATE ─────────────────────────────────────────────────────────────────

    private Booking getBookingByIdOrThrow(UUID id) {
        return bookingRepository.findBookingById(id)
                .orElseThrow(() -> new BookingException(BookingErrorCode.BOOKING_NOT_FOUND, "Booking not found"));
    }

    private boolean isAllTripBookingsCompleted(UUID tripId) {
        return !bookingRepository.existsByTripIdAndStateIn(tripId, List.of(BookingState.ACCEPTED));
    }
}
