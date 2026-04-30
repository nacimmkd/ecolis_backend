package com.deliveryplatform.trips;

import com.deliveryplatform.trips.dto.TripStopRequest;
import com.deliveryplatform.trips.dto.TripStopResponse;
import com.deliveryplatform.trips.dto.TripCreateRequest;
import com.deliveryplatform.trips.dto.TripDetailedResponse;

import java.util.List;
import java.util.UUID;

public interface TripService {

    TripDetailedResponse getTrip(UUID id);

    List<TripDetailedResponse> getUserTrips(UUID userId);

    List<TripDetailedResponse> getAllTrips();

    TripDetailedResponse createTrip(UUID userId, TripCreateRequest request);

    TripDetailedResponse updateTrip(UUID tripId, UUID userId, TripCreateRequest request);

    void deleteTrip(UUID tripId, UUID userId);

    TripStopResponse addStop(UUID tripId, UUID userId, TripStopRequest request);

    TripStopResponse updateStop(UUID stopId, UUID tripId, UUID userId, TripStopRequest request);

    void deleteStop(UUID stopId, UUID tripId, UUID userId);
}