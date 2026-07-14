package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.requests.RequestRepository;
import com.deliveryplatform.trips.Trip;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingRepository        bookingRepository;
    private final RequestRepository        requestRepository;
    private final BookingMapper            bookingMapper;


    @Override
    public BookingDto getBooking(UUID bookingId, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertInvolves(booking, currentUserId);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto create(UUID requestId) {
        var request = requestRepository.findRequestById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("request not found"));
        var booking = Booking.createFromRequest(request);
        request.softDelete();
        bookingRepository.save(booking);
        requestRepository.save(request);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void cancel(UUID bookingId, String reason, UUID currentUserId) {

        var booking = getBookingByIdOrThrow(bookingId);
        assertInvolves(booking, currentUserId);
        assertBookingInStatus(booking, BookingStatus.PENDING, "Only PENDING bookings can be cancelled");

        var cancelledBy = booking.resolveCanceller(currentUserId);
        booking.cancel(reason, cancelledBy);

        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void pay(UUID bookingId, UUID senderId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertIsParcelOwner(booking.getParcel(), senderId);
        assertBookingInStatus(booking, BookingStatus.PENDING, "Only PENDING bookings can be paid");
        booking.pay();
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void confirmPickUp(UUID bookingId, String pickUpCode, UUID userId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertIsTripOwner(booking.getTrip(), userId);
        booking.confirmPickUp(pickUpCode);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void complete(UUID bookingId, String dropOfCode, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertIsTripOwner(booking.getTrip(), currentUserId);
        assertBookingInStatus(booking, BookingStatus.PAID, "Only PAID bookings can be completed");
        booking.complete(dropOfCode);
        bookingRepository.save(booking);
    }

    // PRIVATE ─────────────────────────────────────────────────────────────────

    private Booking getBookingByIdOrThrow(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private void assertIsTripOwner(Trip trip, UUID currentUserId) {
        if (!currentUserId.equals(trip.getOwnerId()))
            throw new UnauthorizedActionException("You are not the carrier of this trip");
    }

    private void assertIsParcelOwner(Parcel parcel, UUID currentUserId) {
        if (!currentUserId.equals(parcel.getOwnerId()))
            throw new UnauthorizedActionException("You are not the sender of this parcel");
    }

    private void assertInvolves(Booking booking, UUID currentUserId) {
        if (booking.involves(currentUserId))
            throw new UnauthorizedActionException("You are not involved in this booking");
    }


    private void assertBookingInStatus(Booking booking, BookingStatus expected, String message) {
        if (!booking.getState().equals(expected))
            throw new InvalidDomainStateException(message);
    }

}