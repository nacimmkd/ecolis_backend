package com.deliveryplatform.trips;

import com.deliveryplatform.trips.dto.TripStopRequest;
import com.deliveryplatform.trips.dto.TripStopResponse;
import com.deliveryplatform.trips.dto.TripCreateRequest;
import com.deliveryplatform.trips.dto.TripResponse;

import java.util.List;
import java.util.UUID;

public interface TripService {

    TripResponse getTrip(UUID id);

    List<TripResponse> getUserTrips(UUID userId);

    List<TripResponse> getAllTrips();

    TripResponse createTrip(UUID userId, TripCreateRequest request);

    TripResponse updateTrip(UUID tripId, UUID userId, TripCreateRequest request);

    void deleteTrip(UUID tripId, UUID userId);

    TripStopResponse addStop(UUID tripId, UUID userId, TripStopRequest request);

    TripStopResponse updateStop(UUID stopId, UUID tripId, UUID userId, TripStopRequest request);

    void deleteStop(UUID stopId, UUID tripId, UUID userId);
}