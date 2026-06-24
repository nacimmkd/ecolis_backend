package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.addresses.AddressService;
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
    private final TripMapper         tripMapper;

    @Override
    public TripDetails getTrip(UUID tripId) {
        var trip = getTripByIdOrThrow(tripId);
        trip.setRemainingWeightKg(tripRepository.getRemainingWeight(tripId));
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
    @Transactional
    public TripDetails createTrip(UUID currentUserId, TripCreateRequest request) {
        var owner = userRepository.findWithProfileById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var trip = tripMapper.toTripEntity(request);
        trip.setOwner(owner);
        trip.setState(TripState.PUBLISHED);
        trip.setDepartureAddress(addressService.geocode(request.departureAddress()));
        trip.setArrivalAddress(addressService.geocode(request.arrivalAddress()));
        trip.addStops(tripMapper.toTripStopEntity(request.stops()));

        return tripMapper.toTripDetailsDto(tripRepository.save(trip));
    }

    @Override
    @Transactional
    public TripDetails updateTrip(UUID tripId, UUID currentUserId, TripUpdateRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);
        assertTripInStatusPublished(trip);

        updateTrip(trip, request);

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
    public StopPointResponse addStop(UUID tripId, UUID currentUserId, AddressRequest address) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var stop = TripStop.builder()
                .order(trip.getStops().size() + 1)
                .address(addressService.geocode(address))
                .build();

        trip.addStop(stop);
        tripRepository.saveAndFlush(trip);
        return tripMapper.toTripStopDto(stop);
    }



    @Override
    @Transactional
    public List<StopPointResponse> updateStops(UUID tripId, UUID currentUserId, List<StopPointRequest> newStops) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var stopEntities = newStops.stream()
                .map(tripMapper::toTripStopEntity)
                .toList();

        trip.removeAllStops();
        trip.addStops(stopEntities);
        tripRepository.save(trip);
        return tripMapper.toTripStopDto(stopEntities);
    }

    @Override
    @Transactional
    public void deleteStop(UUID stopId, UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var stop = findStopInTrip(trip, stopId);
        trip.removeStop(stop);
        trip.reorderStops();

        tripRepository.save(trip);
    }

    // ----------------------------------------------------------------

    private Trip getTripByIdOrThrow(UUID id) {
        return tripRepository.findByIdWithStopsAndOwner(id)
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

    private void updateTrip(Trip trip, TripUpdateRequest request) {
        trip.setDepartureAddress(addressService.geocode(request.departureAddress()));
        trip.setArrivalAddress(addressService.geocode(request.arrivalAddress()));
        trip.setDepartureDate(request.departureDate());
        trip.setArrivalDate(request.arrivalDate());
        trip.setAvailableWeightKg(request.availableWeightKg());
        trip.setPricePerKg(request.pricePerKg());
        trip.setMaxDetourKm(request.maxDetourKm());
        trip.setNotes(request.notes());
        trip.setInstantBooking(request.instantBooking());

        updateStops(trip, request.stops());
    }

    public void updateStops(Trip trip, List<StopPointRequest> newStops) {
        if (newStops == null) return;
        trip.removeAllStops();
        if (!newStops.isEmpty()) {
            trip.addStops(tripMapper.toTripStopEntity(newStops));
        }
    }

}