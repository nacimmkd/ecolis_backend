package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.GeocodingService;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.trips.dto.*;
import com.deliveryplatform.users.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripServiceImp implements TripService {

    private final TripRepository     tripRepository;
    private final TripStopRepository stopRepository;
    private final UserRepository     userRepository;
    private final GeocodingService   geocodingService;
    private final TripMapper         tripMapper;
    private final TripResolver       tripResolver;

    @Override
    public TripDetailedResponse getTrip(UUID tripId) {
        return tripResolver.resolveDetailed(getTripByIdOrThrow(tripId));
    }

    @Override
    public List<TripDetailedResponse> getUserTrips(UUID currentUserId) {
        return tripRepository.findByOwnerId(currentUserId).stream()
                .map(tripResolver::resolveDetailed)
                .toList();
    }

    @Override
    public List<TripDetailedResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(tripResolver::resolveDetailed)
                .toList();
    }

    @Override
    @Transactional
    public TripDetailedResponse createTrip(UUID currentUserId, TripCreateRequest request) {
        var owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var trip = tripMapper.toEntity(request);
        trip.setOwner(owner);
        trip.setStatus(TripStatus.PUBLISHED);
        trip.setDepartureAddress(geocodingService.geocode(request.departureAddress()));
        trip.setArrivalAddress(geocodingService.geocode(request.arrivalAddress()));
        trip.updateStops(buildStopEntities(request.stops()));

        return tripResolver.resolveDetailed(tripRepository.save(trip));
    }

    @Override
    @Transactional
    public TripDetailedResponse updateTrip(UUID tripId, UUID currentUserId, TripUpdateRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);
        assertTripInStatusPublished(trip);

        applyUpdates(trip, request);

        return tripResolver.resolveDetailed(tripRepository.save(trip));
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
    public TripStopResponse addStop(UUID tripId, UUID currentUserId, TripStopRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var stop = TripStop.builder()
                .stopOrder(trip.getStops().size() + 1)
                .address(geocodingService.geocode(request.address()))
                .build();

        trip.addStop(stop);

        return tripMapper.toStopResponse(stopRepository.save(stop))
                .withAddress(stop.getAddress());
    }

    @Override
    @Transactional
    public TripStopResponse updateStop(UUID stopId, UUID tripId, UUID currentUserId, TripStopRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var stop = findStopInTrip(trip, stopId);
        stop.setAddress(geocodingService.geocode(request.address()));

        return tripMapper.toStopResponse(stopRepository.save(stop))
                .withAddress(stop.getAddress());
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

    private List<TripStop> buildStopEntities(List<TripStopRequest> stops) {
        if (stops == null) return List.of();
        return stops.stream()
                .map(req -> TripStop.builder()
                        .stopOrder(req.stopOrder())
                        .address(geocodingService.geocode(req.address()))
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
        if (!trip.getStatus().equals(TripStatus.PUBLISHED))
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

    private void validateStopsSequence(List<TripStopRequest> stops) {
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).stopOrder() != i + 1)
                throw new InvalidDomainStateException("Trip stops must be in sequence order");
        }
    }

    private void applyUpdates(Trip trip, TripUpdateRequest request) {
        if (request.departureAddress()   != null) trip.setDepartureAddress(geocodingService.geocode(request.departureAddress()));
        if (request.arrivalAddress()     != null) trip.setArrivalAddress(geocodingService.geocode(request.arrivalAddress()));
        if (request.departureDate()      != null) trip.setDepartureDate(request.departureDate());
        if (request.arrivalDate()        != null) trip.setArrivalDate(request.arrivalDate());
        if (request.availableVolumeCm3() != null) trip.setAvailableVolumeCm3(request.availableVolumeCm3());
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