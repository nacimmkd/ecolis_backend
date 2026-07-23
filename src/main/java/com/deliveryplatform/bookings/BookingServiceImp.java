package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.events.BookingCanceledEvent;
import com.deliveryplatform.bookings.events.BookingCompletedEvent;
import com.deliveryplatform.bookings.events.BookingCreatedEvent;
import com.deliveryplatform.bookings.exceptions.BookingErrorCode;
import com.deliveryplatform.bookings.exceptions.BookingException;
import com.deliveryplatform.requests.RequestRepository;
import com.deliveryplatform.requests.exceptions.RequestErrorCode;
import com.deliveryplatform.requests.exceptions.RequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingRepository        bookingRepository;
    private final RequestRepository        requestRepository;
    private final BookingMapper            bookingMapper;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public BookingDto getBooking(UUID bookingId, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);
        booking.assertUserInvolved(currentUserId);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto create(UUID requestId) {
        var request = requestRepository.findRequestById(requestId)
                .orElseThrow(() -> new RequestException(RequestErrorCode.REQUEST_NOT_FOUND, "request not found"));
        var booking = Booking.createFromRequest(request);
        request.delete();
        bookingRepository.save(booking);
        requestRepository.save(request);
        eventPublisher.publishEvent(new BookingCreatedEvent(booking.getId(), booking.getSender()));
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public void cancel(UUID bookingId, String reason, UUID currentUserId) {

        var booking = getBookingByIdOrThrow(bookingId);
        booking.assertUserInvolved(currentUserId);
        booking.assertIsInState(BookingState.PENDING, "Only PENDING bookings can be cancelled");

        var cancelledBy = booking.resolveCanceller(currentUserId);
        booking.cancel(reason, cancelledBy);
        eventPublisher.publishEvent(new BookingCanceledEvent(booking.getId(), booking.getSender()));
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void pay(UUID bookingId, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);

        var parcel = booking.getParcel();
        parcel.assertOwnedBy(currentUserId);

        booking.assertIsInState(BookingState.PENDING, "Only PENDING bookings can be paid");
        booking.pay();
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void confirmPickUp(UUID bookingId, String pickUpCode, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);

        var trip = booking.getTrip();
        trip.assertOwnedBy(currentUserId);

        booking.confirmPickUp(pickUpCode);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void complete(UUID bookingId, String dropOfCode, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);

        var trip = booking.getTrip();
        trip.assertOwnedBy(currentUserId);

        booking.assertIsInState(BookingState.PAID, "Only PAID bookings can be completed");
        booking.complete(dropOfCode);
        bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingCompletedEvent(booking.getId(), booking.getSender()));
    }

    // PRIVATE ─────────────────────────────────────────────────────────────────

    private Booking getBookingByIdOrThrow(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingException(BookingErrorCode.BOOKING_NOT_FOUND,"Booking not found"));
    }




}