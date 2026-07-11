package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.addresses.AddressService;
import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingMapper;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.bookings.dto.TripBookingDto;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.trips.dto.*;
import com.deliveryplatform.users.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final BookingMapper      bookingMapper;

    @Override
    public TripDetails getTrip(UUID tripId) {
        var trip = getTripByIdOrThrow(tripId);
        return tripMapper.toTripDetailsDto(trip);
    }

    @Override
    public List<TripSummary> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toTripSummaryDto)
                .toList();
    }

    @Override
    public List<TripSummary> getMyTrips(UUID currentUserId) {
        return tripRepository.findByOwnerId(currentUserId).stream()
                .map(tripMapper::toTripSummaryDto)
                .toList();
    }

    @Override
    public List<TripBookingDto> getTripBookings(UUID tripId, UUID userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        assertOwnership(trip, userId);

        List<Booking> bookings = bookingRepository.findByTripId(tripId);
        return bookingMapper.toTripBookingDto(bookings);
    }

    @Override
    @Transactional
    public TripDetails createTrip(UUID currentUserId, TripCreateRequest request) {
        var owner = userRepository.findUserWithProfileById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var trip = Trip.createFromRequest(
                request,
                addressService.geocode(request.departureAddress()),
                addressService.geocode(request.arrivalAddress()),
                owner
        );
        return tripMapper.toTripDetailsDto(tripRepository.save(trip));
    }

    @Override
    @Transactional
    public TripDetails updateTrip(UUID tripId, UUID currentUserId, TripUpdateRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);
        assertTripInStatusPublished(trip);

        applyUpdates(trip, request);

        return tripMapper.toTripDetailsDto(tripRepository.save(trip));
    }

    @Override
    @Transactional
    public void deleteTrip(UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);
        assertTripInStatusPublished(trip);
        trip.softDelete();
        tripRepository.save(trip);
    }

    @Override
    @Transactional
    public void addStop(UUID tripId, UUID currentUserId, AddressRequest address) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        trip.addStop(addressService.geocode(address));
        tripRepository.save(trip);
    }

    @Override
    @Transactional
    public List<TripStopDto> updateStops(UUID tripId, UUID currentUserId, List<TripStopRequest> newStopsRequest) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var stops = newStopsRequest.stream()
                .map(request -> {
                    var address = addressService.geocode(request.address());
                    return TripStop.create(address, request.order());
                }).toList();

        trip.updateStops(stops);
        var updatedTrip = tripRepository.save(trip);
        return tripMapper.toTripStopDto(updatedTrip.getStops());
    }

    @Override
    @Transactional
    public void deleteStop(UUID stopId, UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var stop = findStopInTrip(trip, stopId);
        trip.removeStopAndReorder(stop);
        tripRepository.save(trip);
    }

    // ----------------------------------------------------------------

    private Trip getTripByIdOrThrow(UUID id) {
        return tripRepository.findTripById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
    }

    private void assertOwnership(Trip trip, UUID userId) {
        if (!trip.getOwner().getId().equals(userId))
            throw new UnauthorizedActionException("User is not owner of this trip");
    }

    private void assertTripInStatusPublished(Trip trip) {
        if (!trip.getState().equals(TripState.PUBLISHED))
            throw new InvalidDomainStateException("Trip is not published");
    }

    private TripStop findStopInTrip(Trip trip, UUID stopId) {
        return trip.getStops().stream()
                .filter(s -> s.getId().equals(stopId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Trip stop not found"));
    }

    private void applyUpdates(Trip trip, TripUpdateRequest request) {
        trip.setDepartureAddress(addressService.geocode(request.departureAddress()));
        trip.setArrivalAddress(addressService.geocode(request.arrivalAddress()));
        trip.setDepartureDate(request.departureDate());
        trip.setArrivalDate(request.arrivalDate());
        trip.updateAvailableWeightKg(request.availableWeightKg());
        trip.setPricePerKg(request.pricePerKg());
        trip.setMaxDetourKm(request.maxDetourKm());
        trip.setNotes(request.notes());
        trip.setInstantBooking(request.instantBooking());
    }

}