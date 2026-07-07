package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.ParcelState;
import com.deliveryplatform.requests.Request;
import com.deliveryplatform.trips.Trip;
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
        assertIsTripOwner(trip, currentUserId);
        return bookingMapper.toDto(bookingRepository.findByTripId(tripId));
    }

    @Override
    public BookingDto getParcelBooking(UUID parcelId, UUID currentUserId) {
        var parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));

        assertIsParcelOwner(parcel, currentUserId);
        return bookingMapper.toDto(bookingRepository.findByParcelId(parcelId));
    }

    @Override
    @Transactional
    public BookingDto create(Request request) {
        var booking = Booking.createFromRequest(request);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void cancel(UUID bookingId, String reason, UUID currentUserId) {

        var booking = getBookingByIdOrThrow(bookingId);
        assertInvolves(booking.involves(currentUserId));
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
    public void confirmDropOff(UUID bookingId, String dropOffCode, UUID userId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertIsTripOwner(booking.getTrip(), userId);
        booking.confirmDropOff(dropOffCode);
        bookingRepository.save(booking);
    }


    @Override
    @Transactional
    public void complete(UUID bookingId, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertIsTripOwner(booking.getTrip(), currentUserId);
        assertBookingInStatus(booking, BookingStatus.PAID, "Only PAID bookings can be completed");
        booking.complete();
        booking.getParcel().updateState(ParcelState.DELIVERED);
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

    private void assertInvolves(boolean involves) {
        if (!involves)
            throw new UnauthorizedActionException("You are not involved in this booking");
    }


    private void assertBookingInStatus(Booking booking, BookingStatus expected, String message) {
        if (!booking.getStatus().equals(expected))
            throw new InvalidDomainStateException(message);
    }

}