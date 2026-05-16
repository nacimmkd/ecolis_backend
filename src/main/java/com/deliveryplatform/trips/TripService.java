package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.trips.dto.*;

import java.util.List;
import java.util.UUID;

public interface TripService {

    TripDetails getTrip(UUID id);

    List<TripSummary> getUserTrips(UUID userId);

    List<TripSummary> getAllTrips();

    TripDetails createTrip(UUID userId, TripCreateRequest request);

    TripDetails updateTrip(UUID tripId, UUID userId, TripUpdateRequest request);

    void deleteTrip(UUID tripId, UUID userId);

    TripStopSummary addStop(UUID tripId, UUID userId, Address address);

    TripStopSummary updateStop(UUID stopId, UUID tripId, UUID userId, TripStopRequest request);

    void deleteStop(UUID stopId, UUID tripId, UUID userId);
}