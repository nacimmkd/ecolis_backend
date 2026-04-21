package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.trips.dto.StopRequest;
import com.deliveryplatform.trips.dto.StopResponse;
import com.deliveryplatform.trips.dto.TripRequest;
import com.deliveryplatform.trips.dto.TripResponse;
import com.deliveryplatform.users.UserServiceImp;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripStopRepository stopRepository;
    private final TripMapper     tripMapper;
    private final TripStopMapper stopMapper;
    private final UserServiceImp userService;


    public TripResponse getTrip(UUID id) {
        return tripMapper.toResponse(getTripByIdOrThrow(id));
    }

    public List<TripResponse> getUserTrips(UUID userId) {
        return tripRepository.findByUserId(userId).stream()
                .map(tripMapper::toResponse)
                .toList();
    }

    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toResponse)
                .toList();
    }


    public List<TripResponse> getAvailableTrips() {
        return tripRepository.findAllTripsByStatus(TripStatus.PUBLISHED)
                .stream()
                .map(tripMapper::toResponse)
                .toList();
    }


    @Transactional
    public TripResponse createTrip(UUID userId, TripRequest request) {
        var user = userService.getUserByIdOrThrow(userId);
        var trip = tripMapper.toEntity(request);
        trip.setUser(user);

        trip.addStops(stopMapper.toEntityList(request.stops()));

        return tripMapper.toResponse(tripRepository.save(trip));
    }


    @Transactional
    public TripResponse updateTrip(UUID tripId, UUID userId, TripRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, userId);
        assertTripInStatus(trip, TripStatus.PUBLISHED, "Trip is not published");
        validateStopsSequence(request.stops());
        tripMapper.updateEntity(trip, request);
        trip.updateStops(stopMapper.toEntityList(request.stops()));
        return tripMapper.toResponse(tripRepository.save(trip));
    }

    @Transactional
    public void deleteTrip(UUID tripId, UUID userId) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, userId);
        assertTripInStatus(trip, TripStatus.PUBLISHED, "Trip is not published");
        tripRepository.delete(trip);
    }

    @Transactional
    public void updateStatus(UUID tripId, TripStatus status) {
        var trip = getTripByIdOrThrow(tripId);
        trip.setStatus(status);
        tripRepository.save(trip);
    }

    // ----------------------------------------------------------------
    // STOPS
    // ----------------------------------------------------------------

    @Transactional
    public StopResponse addStop(UUID tripId, UUID userId, Address address) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, userId);
        int order = trip.getStops().size() + 1 ;
        var stop = TripStop.builder()
                .stopOrder(order)
                //.address(addressMapper.toEntity(address))
                .build();
        trip.addStop(stop);
        return stopMapper.toResponse(stopRepository.save(stop));
    }


    @Transactional
    public void deleteStop(UUID stopId, UUID tripId, UUID userId) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, userId);
        removeAndReorder(trip, stopId);
        tripRepository.save(trip);
    }


    @Transactional
    public StopResponse updateStop(UUID stopId, UUID tripId, UUID userId, Address address) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, userId);
        var stop = findStopInTrip(trip, stopId);
        //stop.setAddress(addressMapper.toEntity(address));
        return stopMapper.toResponse(stopRepository.save(stop));
    }


    // ----------------------------------------------------------------
    // PRIVATE
    // ----------------------------------------------------------------

    public Trip getTripByIdOrThrow(UUID id) {
        return tripRepository.findByIdWithStops(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip"));
    }

    private void assertOwnership(Trip trip, UUID userId) {
        if (!trip.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("User is not owner of this trip");
        }
    }

    private void assertTripInStatus(Trip trip, TripStatus expected, String message) {
        if (!trip.getStatus().equals(expected)) {
            throw new UnauthorizedActionException(message);
        }
    }

    private TripStop findStopInTrip(Trip trip, UUID stopId) {
        return trip.getStops().stream()
                .filter(stop -> stop.getId().equals(stopId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Trip stop"));
    }

    private void removeAndReorder(Trip trip, UUID stopId) {
        var stopToRemove = findStopInTrip(trip, stopId);
        trip.removeStop(stopToRemove);
        trip.reorderStops();
    }

    private void validateStopsSequence(List<StopRequest> stops) {
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).stopOrder() != i + 1) {
                throw new InvalidDomainStateException("Trip stop must be in sequence order");
            }
        }
    }


}