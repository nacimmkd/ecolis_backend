package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingRequest;
import com.deliveryplatform.bookings.dto.BookingResponse;
import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.ParcelServiceImp;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.trips.Trip;
import com.deliveryplatform.trips.TripRepository;
import com.deliveryplatform.trips.TripServiceImp;
import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserServiceImp;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ParcelRepository parcelRepository;
    private final TripRepository tripRepository;

    private static final Set<BookingStatus> TERMINAL_STATUSES = Set.of(
            BookingStatus.COMPLETED,
            BookingStatus.CANCELLED,
            BookingStatus.REJECTED
    );


    public BookingResponse getBookingById(UUID bookingId) {
        return bookingMapper.toDto(getBookingByIdOrThrow(bookingId));
    }


    @Transactional
    public BookingResponse create(BookingRequest request, UUID requesterId) {
        validateNoDuplicateBooking(request);

        var parcel = parcelRepository.findById(request.parcelId())
                .orElseThrow(() -> new ResourceNotFoundException("Parcel Not Found"));
        assertParcelOwner(parcel, requesterId);

        var trip = tripRepository.findById(request.tripId()).orElseThrow(() -> new ResourceNotFoundException("Trip Not Found"));
        var requester = parcel.getUser();
        var requested = trip.getOwner();
        var booking = buildBooking(parcel, trip, requester , requested);

        // For instant bookings, the parcel is immediately reserved
        if (trip.isInstantBooking()) {
            parcel.setStatus(ParcelStatus.BOOKED);
        }

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public void accept(UUID bookingId, UUID carrierId) {
        var booking = getBookingByIdOrThrow(bookingId);

        assertTripOwner(booking, carrierId);
        assertBookingInStatus(booking, BookingStatus.PENDING, "Only PENDING bookings can be accepted");

        booking.setStatus(BookingStatus.ACCEPTED);
        booking.setAcceptedAt(OffsetDateTime.now());
        booking.getParcel().setStatus(ParcelStatus.BOOKED);

        bookingRepository.save(booking);
    }

    @Transactional
    public void reject(UUID bookingId, UUID carrierId) {
        var booking = getBookingByIdOrThrow(bookingId);

        assertTripOwner(booking, carrierId);
        assertBookingInStatus(booking, BookingStatus.PENDING, "Only PENDING bookings can be rejected");

        booking.setStatus(BookingStatus.REJECTED);

        bookingRepository.save(booking);
    }

    @Transactional
    public void cancel(UUID bookingId, UUID requesterId) {
        var booking = getBookingByIdOrThrow(bookingId);

        assertCancellationAllowed(booking, requesterId);

        booking.setStatus(BookingStatus.CANCELLED);
        // Free the parcel if it was already reserved
        booking.getParcel().setStatus(ParcelStatus.PUBLISHED);

        bookingRepository.save(booking);
    }

    @Transactional
    public void complete(UUID bookingId, UUID carrierId) {
        var booking = getBookingByIdOrThrow(bookingId);

        assertTripOwner(booking, carrierId);
        assertBookingInStatus(booking, BookingStatus.PAID, "Only PAID bookings can be completed");

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletedAt(OffsetDateTime.now());
        booking.getParcel().setStatus(ParcelStatus.DELIVERED);

        bookingRepository.save(booking);
    }


    // -------------------------------------------------------------------------

    private Booking getBookingByIdOrThrow(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking"));
    }

    private Booking buildBooking(Parcel parcel, Trip trip, User sender, User carrier) {
        var status = trip.isInstantBooking() ? BookingStatus.ACCEPTED : BookingStatus.PENDING; // BUG FIX: was REJECTED
        return Booking.builder()
                .trip(trip)
                .parcel(parcel)
                .sender(sender)
                .carrier(carrier)
                .status(status)
                .price(calculatePrice(parcel, trip))
                .acceptedAt(status == BookingStatus.ACCEPTED ? OffsetDateTime.now() : null)
                .build();
    }

    private BigDecimal calculatePrice(Parcel parcel, Trip trip) {
        return parcel.getWeightKg().multiply(trip.getPricePerKg());
    }

    private void validateNoDuplicateBooking(BookingRequest request) {
        if (bookingRepository.existsByParcelIdAndTripId(request.parcelId(), request.tripId())) {
            throw new ConflictException("Booking already exists for this parcel and trip");
        }
    }

    private void assertParcelOwner(Parcel parcel, UUID requesterId) {
        if (!parcel.getUser().getId().equals(requesterId)) {
            throw new UnauthorizedActionException("Requester is not the owner of the parcel");
        }
    }

    private void assertTripOwner(Booking booking, UUID carrierId) {
        if (!booking.getTrip().getOwner().getId().equals(carrierId)) {
            throw new UnauthorizedActionException("User is not the carrier of this trip");
        }
    }

    private void assertBookingInStatus(Booking booking, BookingStatus expected, String message) {
        if (!booking.getStatus().equals(expected)) {
            throw new UnauthorizedActionException(message);
        }
    }

    private void assertCancellationAllowed(Booking booking, UUID userId) {
        boolean isSender = booking.getSender().getId().equals(userId);
        boolean isCarrier   = booking.getTrip().getOwner().getId().equals(userId);

        if (!isSender && !isCarrier) {
            throw new UnauthorizedActionException("Only the requester or carrier can cancel a booking");
        }

        if (TERMINAL_STATUSES.contains(booking.getStatus())) {
            throw new UnauthorizedActionException(
                    "Cannot cancel a booking with status: " + booking.getStatus()
            );
        }
    }
}