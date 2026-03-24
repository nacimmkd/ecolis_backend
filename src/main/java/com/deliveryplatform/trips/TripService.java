package com.deliveryplatform.trips;

import com.deliveryplatform.trips.TripDto.*;
import com.deliveryplatform.trips.exceptions.IllegalTripStateException;
import com.deliveryplatform.trips.exceptions.TripNotFoundException;
import com.deliveryplatform.trips.exceptions.UnauthorizedTripActionException;
import com.deliveryplatform.users.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper     tripMapper;
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
    // PRIVATE
    // ----------------------------------------------------------------

    private Trip getTripOrThrow(UUID id) {
        return tripRepository.findById(id)
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
}