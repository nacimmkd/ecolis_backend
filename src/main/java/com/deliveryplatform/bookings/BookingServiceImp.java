package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingRequestCreateRequest;
import com.deliveryplatform.bookings.dto.BookingRequestResponse;
import com.deliveryplatform.bookings.dto.BookingResponse;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.trips.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingRepository        bookingRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final ParcelRepository         parcelRepository;
    private final TripRepository           tripRepository;
    private final BookingResolver          bookingResolver;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public BookingRequestResponse getBookingRequest(UUID requestId, UUID currentUserId) {
        var request = getRequestByIdOrThrow(requestId);
        assertInvolves(request.involves(currentUserId));
        return bookingResolver.resolve(request);
    }

    @Override
    @Transactional
    public BookingRequestResponse createRequest(BookingRequestCreateRequest dto, UUID senderId) {
        var parcel = parcelRepository.findById(dto.parcelId())
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));

        assertParcelOwner(parcel.getOwner().getId(), senderId);
        assertParcelAvailable(parcel.getStatus());

        var trip = tripRepository.findById(dto.tripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        assertNoDuplicateRequest(dto.parcelId(), dto.tripId());

        var bookingRequest = BookingRequest.create(trip, parcel);

        if (trip.isInstantBooking()) {
            bookingRequest.accept();
            parcel.setStatus(ParcelStatus.BOOKED);
            bookingRequestRepository.save(bookingRequest);
            bookingRepository.save(buildBookingFromRequest(bookingRequest));
        } else {
            bookingRequestRepository.save(bookingRequest);
        }

        return bookingResolver.resolve(bookingRequest);
    }

    @Override
    @Transactional
    public void cancelRequest(UUID requestId, UUID userId) {
        var request = getRequestByIdOrThrow(requestId);
        assertInvolves(request.involves(userId));
        assertRequestIsPending(request);
        request.cancel();
        bookingRequestRepository.save(request);
    }

    @Override
    @Transactional
    public BookingResponse acceptRequest(UUID requestId, UUID carrierId) {
        var request = getRequestByIdOrThrow(requestId);
        assertCarrier(request.getCarrierId(), carrierId);
        assertRequestIsPending(request);

        request.accept();
        request.getParcel().setStatus(ParcelStatus.BOOKED);
        bookingRequestRepository.save(request);

        var booking = bookingRepository.save(Booking.createFromRequest(request));
        return bookingResolver.resolve(booking);
    }

    @Override
    @Transactional
    public void rejectRequest(UUID requestId, UUID carrierId, String reason) {
        var request = getRequestByIdOrThrow(requestId);
        assertCarrier(request.getCarrierId(), carrierId);
        assertRequestIsPending(request);
        request.reject(reason);
        bookingRequestRepository.save(request);
    }

    @Override
    public BookingResponse getBooking(UUID bookingId, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertInvolves(booking.involves(currentUserId));
        return bookingResolver.resolve(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(UUID bookingId, UUID userId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertInvolves(booking.involves(userId));
        assertBookingInStatus(booking, BookingStatus.CONFIRMED, "Only CONFIRMED bookings can be cancelled");
        booking.cancel();
        booking.getParcel().setStatus(ParcelStatus.PUBLISHED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void pay(UUID bookingId, UUID senderId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertSender(booking.getParcel().getOwner().getId(), senderId);
        assertBookingInStatus(booking, BookingStatus.CONFIRMED, "Only CONFIRMED bookings can be paid");
        booking.pay();
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void complete(UUID bookingId, UUID carrierId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertCarrier(booking.getTrip().getOwner().getId(), carrierId);
        assertBookingInStatus(booking, BookingStatus.PAID, "Only PAID bookings can be completed");
        booking.complete();
        booking.getParcel().setStatus(ParcelStatus.DELIVERED);
        bookingRepository.save(booking);
    }

    // PRIVATE ─────────────────────────────────────────────────────────────────

    private Booking getBookingByIdOrThrow(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private BookingRequest getRequestByIdOrThrow(UUID id) {
        return bookingRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking request not found"));
    }

    private void assertNoDuplicateRequest(UUID parcelId, UUID tripId) {
        if (bookingRequestRepository.existsByParcelIdAndTripId(parcelId, tripId))
            throw new ConflictException("A booking request already exists for this parcel and trip");
    }

    private void assertParcelOwner(UUID ownerId, UUID requesterId) {
        if (!ownerId.equals(requesterId))
            throw new UnauthorizedActionException("You are not the owner of this parcel");
    }

    private void assertParcelAvailable(ParcelStatus status) {
        if (!ParcelStatus.PUBLISHED.equals(status))
            throw new InvalidDomainStateException("Parcel is not available for booking");
    }

    private void assertCarrier(UUID carrierId, UUID userId) {
        if (!carrierId.equals(userId))
            throw new UnauthorizedActionException("You are not the carrier of this trip");
    }

    private void assertSender(UUID senderId, UUID userId) {
        if (!senderId.equals(userId))
            throw new UnauthorizedActionException("You are not the sender of this booking");
    }

    private void assertInvolves(boolean involves) {
        if (!involves)
            throw new UnauthorizedActionException("You are not involved in this booking");
    }

    private void assertRequestIsPending(BookingRequest request) {
        if (!request.isPending())
            throw new InvalidDomainStateException(
                    "Booking request is not pending, current status: " + request.getStatus()
            );
    }

    private void assertBookingInStatus(Booking booking, BookingStatus expected, String message) {
        if (!booking.getStatus().equals(expected))
            throw new InvalidDomainStateException(message);
    }

    private Booking buildBookingFromRequest(BookingRequest request) {
        var booking = Booking.createFromRequest(request);
        booking.setPickupCode(CodeGeneratorUtil.generateParcelCode());
        booking.setDropOffCode(CodeGeneratorUtil.generateParcelCode());
        booking.setPrice(BookingPriceCalculator.calculate(request.getParcel(), request.getTrip()));
        return booking;
    }
}