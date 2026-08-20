package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.addresses.AddressService;
import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.bookings.BookingState;
import com.deliveryplatform.trips.dto.*;
import com.deliveryplatform.trips.exceptions.TripErrorCode;
import com.deliveryplatform.trips.exceptions.TripException;
import com.deliveryplatform.users.UserRepository;
import com.deliveryplatform.users.exceptions.UserErrorCode;
import com.deliveryplatform.users.exceptions.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripServiceImp implements TripService {

    private final TripRepository     tripRepository;
    private final UserRepository     userRepository;
    private final AddressService     addressService;
    private final BookingRepository  bookingRepository;
    private final TripMapper         tripMapper;
    private final TripBookingMapper  tripBookingMapper;

    @Override
    public TripDetails getTrip(UUID tripId) {
        var trip = getTripByIdOrThrow(tripId);
        var newRequestCount = bookingRepository.countByTripIdAndState(tripId, BookingState.WAITING_FOR_ANSWER);
        var acceptedBookingsCount = bookingRepository.countByTripIdAndState(tripId, BookingState.ACCEPTED);
        return tripMapper.toTripDetailsDto(trip, newRequestCount, acceptedBookingsCount);
    }

    @Override
    public Page<TripSummary> getAllTrips(Pageable pageable) {
        return tripRepository.findAll(pageable).map(tripMapper::toTripSummaryDto);
    }


    @Override
    public Page<TripSummary> getMyTrips(UUID currentUserId, TripState state, Pageable pageable) {
        Page<Trip> trips = (state == null)
                ? tripRepository.findTripByOwner_Id(currentUserId, pageable)
                : tripRepository.findTripByOwner_IdAndState(currentUserId, state, pageable);
        return trips.map(tripMapper::toTripSummaryDto);
    }


    @Override
    public Page<TripBookingDto> getTripBookings(UUID tripId, UUID userId, Pageable pageable) {
        var trip = getTripByIdOrThrow(tripId);
        trip.assertOwnedBy(userId);

        Page<Booking> bookings = bookingRepository.findByTripIdAndStateIn(
                tripId,
                List.of(BookingState.WAITING_FOR_ANSWER, BookingState.ACCEPTED, BookingState.REJECTED, BookingState.CANCELLED, BookingState.COMPLETED),
                pageable
        );

        return bookings.map(tripBookingMapper::toTripBookingDto);
    }

    @Override
    @Transactional
    public TripDetails createTrip(UUID currentUserId, TripCreateRequest request) {
        var owner = userRepository.findUserById(currentUserId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "trip owner not found"));

        var trip = Trip.createFromRequest(
                request,
                addressService.geocode(request.departureAddress()),
                addressService.geocode(request.arrivalAddress()),
                owner
        );
        return tripMapper.toTripDetailsDto(tripRepository.save(trip), 0, 0);
    }

    @Override
    @Transactional
    public TripDetails updateTrip(UUID tripId, UUID currentUserId, TripUpdateRequest request) {
        var trip = getTripByIdOrThrow(tripId);

        trip.update(
                currentUserId,
                addressService.geocode(request.departureAddress()),
                addressService.geocode(request.arrivalAddress()),
                request.departureDate(),
                request.arrivalDate(),
                request.availableWeightKg(),
                request.pricePerKg(),
                request.maxDetourKm(),
                request.notes(),
                request.instantBooking()
        );

        var newRequestCount = bookingRepository.countByTripIdAndState(tripId, BookingState.WAITING_FOR_ANSWER);
        var acceptedBookingsCount = bookingRepository.countByTripIdAndState(tripId, BookingState.ACCEPTED);

        tripRepository.save(trip);
        return tripMapper.toTripDetailsDto(trip, newRequestCount, acceptedBookingsCount);
    }

    @Override
    @Transactional
    public void deleteTrip(UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        trip.delete(currentUserId);
        tripRepository.save(trip);
    }

    @Override
    @Transactional
    public void addStop(UUID tripId, UUID currentUserId, AddressRequest address) {
        var trip = getTripByIdOrThrow(tripId);
        trip.addStop(currentUserId, addressService.geocode(address));
        tripRepository.save(trip);
    }

    @Override
    @Transactional
    public void deleteStop(UUID stopId, UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        trip.removeStop(currentUserId, stopId);
        tripRepository.save(trip);
    }

    // ----------------------------------------------------------------

    private Trip getTripByIdOrThrow(UUID id) {
        return tripRepository.findTripById(id)
                .orElseThrow(() -> new TripException(TripErrorCode.TRIP_NOT_FOUND, "Trip Not found"));
    }
}