package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.AddressRequest;
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

    void addStop(UUID tripId, UUID userId, AddressRequest address);

    List<TripStopDto>  updateStops(UUID tripId, UUID userId, List<TripStopRequest> newStops);

    void deleteStop(UUID stopId, UUID tripId, UUID userId);
}