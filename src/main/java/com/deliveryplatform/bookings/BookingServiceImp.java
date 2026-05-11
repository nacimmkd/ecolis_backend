package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.requests.BookingRequestRepository;
import com.deliveryplatform.trips.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingRepository        bookingRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final ParcelRepository         parcelRepository;
    private final TripRepository           tripRepository;
    private final BookingMapper            bookingMapper;




    @Override
    public BookingDto getBooking(UUID bookingId, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertInvolves(booking.involves(currentUserId));
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getMyBookings(UUID currentUserId) {
        return bookingMapper.toDto(bookingRepository.findAllByInvolvedUser(currentUserId));
    }

    @Override
    public List<BookingDto> getTripBookings(UUID tripId, UUID currentUserId) {
        var trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        assertIsCarrier(trip.getOwner().getId(), currentUserId);
        return bookingMapper.toDto(bookingRepository.findByTripId(tripId));
    }

    @Override
    public BookingDto getParcelBooking(UUID parcelId, UUID currentUserId) {
        var parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));

        assertIsSender(parcel.getOwner().getId(), currentUserId);
        return bookingMapper.toDto(bookingRepository.findByParcelId(parcelId));
    }

    @Override
    @Transactional
    public void cancel(UUID bookingId, UUID userId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertInvolves(booking.involves(userId));
        assertBookingInStatus(booking, BookingStatus.PENDING, "Only PENDING bookings can be cancelled");
        booking.cancel();
        booking.getParcel().setStatus(ParcelStatus.PUBLISHED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void pay(UUID bookingId, UUID senderId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertIsSender(booking.getParcel().getOwner().getId(), senderId);
        assertBookingInStatus(booking, BookingStatus.PENDING, "Only PENDING bookings can be paid");
        booking.pay();
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void complete(UUID bookingId, UUID carrierId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertIsCarrier(booking.getTrip().getOwner().getId(), carrierId);
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

    private void assertIsCarrier(UUID carrierId, UUID currentUserId) {
        if (!carrierId.equals(currentUserId))
            throw new UnauthorizedActionException("You are not the carrier of this trip");
    }

    private void assertIsSender(UUID senderId, UUID currentUserId) {
        if (!senderId.equals(currentUserId))
            throw new UnauthorizedActionException("You are not the sender of this booking");
    }

    private void assertInvolves(boolean involves) {
        if (!involves)
            throw new UnauthorizedActionException("You are not involved in this booking");
    }


    private void assertBookingInStatus(Booking booking, BookingStatus expected, String message) {
        if (!booking.getStatus().equals(expected))
            throw new InvalidDomainStateException(message);
    }

}