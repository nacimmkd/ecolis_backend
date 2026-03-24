package com.deliveryplatform.trips;

import com.deliveryplatform.common.addresses.AddressMapper;
import com.deliveryplatform.common.addresses.Address;
import com.deliveryplatform.trips.TripDto.*;
import com.deliveryplatform.trips.exceptions.IllegalTripStateException;
import com.deliveryplatform.trips.exceptions.TripNotFoundException;
import com.deliveryplatform.trips.exceptions.TripStopNotFoundException;
import com.deliveryplatform.trips.exceptions.UnauthorizedTripActionException;
import com.deliveryplatform.users.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.deliveryplatform.trips.TripStopDto.*;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripStopRepository stopRepository;
    private final TripMapper     tripMapper;
    private final TripStopMapper stopMapper;
    private final AddressMapper addressMapper;
    private final UserService    userService;


    public TripResponse getTrip(UUID id) {
        return tripMapper.toResponse(getTripOrThrow(id));
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

    public Trip getTripEntity(UUID id) {
        return tripRepository.findById(id).orElseThrow(TripNotFoundException::new);
    }


    @Transactional
    public TripResponse createTrip(UUID userId, TripRequest request) {
        var user = userService.getUserByIdOrThrow(userId);
        var trip = tripMapper.toEntity(request);
        trip.setUser(user);

        if (trip.getStops() != null) {
            trip.getStops().forEach(stop -> stop.setTrip(trip));
        }

        return tripMapper.toResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripResponse updateTrip(UUID tripId, UUID userId, TripRequest request) {
        var trip = getTripOrThrow(tripId);
        assertOwnership(trip, userId);
        assertTripIsPublished(trip);
        tripMapper.updateEntity(trip, request);
        return tripMapper.toResponse(tripRepository.save(trip));
    }

    @Transactional
    public void deleteTrip(UUID tripId, UUID userId) {
        var trip = getTripOrThrow(tripId);
        assertOwnership(trip, userId);
        assertTripIsPublished(trip);
        tripRepository.delete(trip);
    }

    @Transactional
    public void updateStatus(UUID tripId, TripStatus status) {
        var trip = getTripOrThrow(tripId);
        trip.setStatus(status);
        tripRepository.save(trip);
    }

    // ----------------------------------------------------------------
    // STOPS
    // ----------------------------------------------------------------

    @Transactional
    public StopResponse addStop(UUID tripId, UUID userId, Address address) {
        var trip = getTripOrThrow(tripId);
        assertOwnership(trip, userId);

        int order = trip.getStops().size() + 1 ;
        var stop = TripStop.builder()
                .trip(trip)
                .stopOrder(order)
                .address(addressMapper.toEntity(address))
                .build();

        trip.addStop(stop);
        return stopMapper.toResponse(stopRepository.save(stop));
    }


    @Transactional
    public void deleteStop(UUID stopId, UUID tripId, UUID userId) {
        var trip = getTripOrThrow(tripId);
        assertOwnership(trip, userId);

        var stopToRemove = findStopInTrip(trip, stopId);
        removeAndReorder(trip, stopToRemove);

        tripRepository.save(trip);
    }


    @Transactional
    public StopResponse updateStop(UUID stopId, UUID tripId, UUID userId, Address address) {
        var trip = getTripOrThrow(tripId);
        assertOwnership(trip, userId);

        var stop = findStopInTrip(trip, stopId);
        stop.setAddress(addressMapper.toEntity(address));

        return stopMapper.toResponse(stopRepository.save(stop));
    }




    // ----------------------------------------------------------------
    // PRIVATE
    // ----------------------------------------------------------------

    private Trip getTripOrThrow(UUID id) {
        return tripRepository.findByIdWithStops(id)
                .orElseThrow(TripNotFoundException::new);
    }

    private void assertOwnership(Trip trip, UUID userId) {
        if (!trip.getUser().getId().equals(userId)) {
            throw new UnauthorizedTripActionException();
        }
    }

    private void assertTripIsPublished(Trip trip) {
        if (trip.getStatus() != TripStatus.PUBLISHED) {
            throw new IllegalTripStateException();
        }
    }

    private TripStop findStopInTrip(Trip trip, UUID stopId) {
        return trip.getStops().stream()
                .filter(stop -> stop.getId().equals(stopId))
                .findFirst()
                .orElseThrow(TripStopNotFoundException::new);
    }

    private void removeAndReorder(Trip trip, TripStop stopToRemove) {
        trip.removeStop(stopToRemove);
        stopRepository.delete(stopToRemove);
        stopRepository.flush();

        trip.getStops().stream()
                .filter(stop -> stop.getStopOrder() > stopToRemove.getStopOrder())
                .forEach(stop -> stop.setStopOrder(stop.getStopOrder() - 1));
    }
}