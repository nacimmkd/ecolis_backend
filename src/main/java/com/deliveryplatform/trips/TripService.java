package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.trips.dto.*;

import java.util.List;
import java.util.UUID;

public interface TripService {

    TripDetails getTrip(UUID id);

    List<TripSummary> getAllTrips();

    List<TripSummary> getMyTrips(UUID currentUserId);

    TripDetails createTrip(UUID userId, TripCreateRequest request);

    TripDetails updateTrip(UUID tripId, UUID userId, TripUpdateRequest request);

    void deleteTrip(UUID tripId, UUID userId);

    StopPoint addStop(UUID tripId, UUID userId, Address address);

    StopPoint updateStop(UUID stopId, UUID tripId, UUID userId, StopPointRequest request);

    void deleteStop(UUID stopId, UUID tripId, UUID userId);
}