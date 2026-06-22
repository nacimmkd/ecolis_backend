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
    private final AddressService addressService;
    private final TripMapper         tripMapper;

    @Override
    public TripDetails getTrip(UUID tripId) {
        var trip = getTripByIdOrThrow(tripId);
        trip.setRemainingWeightKg(tripRepository.getRemainingWeight(tripId));
        return tripMapper.toDetailsDto(trip);
    }

    @Override
    public List<TripSummary> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toSummaryDto)
                .toList();
    }

    @Override
    public List<TripSummary> getMyTrips(UUID currentUserId) {
        return tripRepository.findByOwnerId(currentUserId).stream()
                .map(tripMapper::toSummaryDto)
                .toList();
    }

    @Override
    @Transactional
    public TripDetails createTrip(UUID currentUserId, TripCreateRequest request) {
        var owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var trip = tripMapper.toEntity(request);
        trip.setOwner(owner);
        trip.setState(TripState.PUBLISHED);
        trip.setDepartureAddress(addressService.geocode(request.departureAddress()));
        trip.setArrivalAddress(addressService.geocode(request.arrivalAddress()));
        trip.updateStops(buildStopEntities(request.stops()));

        return tripMapper.toDetailsDto(tripRepository.save(trip));
    }

    @Override
    @Transactional
    public TripDetails updateTrip(UUID tripId, UUID currentUserId, TripUpdateRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);
        assertTripInStatusPublished(trip);

        applyUpdates(trip, request);

        return tripMapper.toDetailsDto(tripRepository.save(trip));
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
    public StopPoint addStop(UUID tripId, UUID currentUserId, AddressRequest address) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var stop = TripStop.builder()
                .stopOrder(trip.getStops().size() + 1)
                .address(addressService.geocode(address))
                .build();

        trip.addStop(stop);
        tripRepository.save(trip);
        return tripMapper.toSummaryDto(stop);
    }

    @Override
    @Transactional
    public StopPoint updateStop(UUID stopId, UUID tripId, UUID currentUserId, StopPointRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var stop = findStopInTrip(trip, stopId);
        stop.setAddress(addressService.geocode(request.address()));
        tripRepository.save(trip);
        return tripMapper.toSummaryDto(stop);
    }

    @Override
    @Transactional
    public void deleteStop(UUID stopId, UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);
        removeAndReorder(trip, stopId);
        tripRepository.save(trip);
    }

    // ----------------------------------------------------------------

    private List<TripStop> buildStopEntities(List<StopPointRequest> stops) {
        if (stops == null) return List.of();
        return stops.stream()
                .map(req -> TripStop.builder()
                        .stopOrder(req.stopOrder())
                        .address(addressService.geocode(req.address()))
                        .build())
                .toList();
    }

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

    private void removeAndReorder(Trip trip, UUID stopId) {
        trip.removeStop(findStopInTrip(trip, stopId));
        trip.reorderStops();
    }

    private void validateStopsSequence(List<StopPointRequest> stops) {
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).stopOrder() != i + 1)
                throw new InvalidDomainStateException("Trip stops must be in sequence order");
        }
    }

    private void applyUpdates(Trip trip, TripUpdateRequest request) {
        if (request.departureAddress()   != null) trip.setDepartureAddress(addressService.geocode(request.departureAddress()));
        if (request.arrivalAddress()     != null) trip.setArrivalAddress(addressService.geocode(request.arrivalAddress()));
        if (request.departureDate()      != null) trip.setDepartureDate(request.departureDate());
        if (request.arrivalDate()        != null) trip.setArrivalDate(request.arrivalDate());
        if (request.availableWeightKg()  != null) trip.setAvailableWeightKg(request.availableWeightKg());
        if (request.pricePerKg()         != null) trip.setPricePerKg(request.pricePerKg());
        if (request.maxDetourKm()        != null) trip.setMaxDetourKm(request.maxDetourKm());
        if (request.notes()              != null) trip.setNotes(request.notes());
        trip.setInstantBooking(request.instantBooking());

        if (request.stops() != null) {
            validateStopsSequence(request.stops());
            trip.updateStops(buildStopEntities(request.stops()));
        }
    }
}