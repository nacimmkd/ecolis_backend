package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.trips.dto.TripBookingDto;
import com.deliveryplatform.trips.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TripService {

    TripDetails getTrip(UUID id);

    Page<TripSummary> getAllTrips(Pageable pageable);

    Page<TripSummary> getMyTrips(UUID currentUserId, TripState state, Pageable pageable);

    Page<TripBookingDto> getTripBookings(UUID tripId, UUID currentUserId, Pageable pageable);

    Page<TripBookingDto> getRequests(UUID tripId, UUID currentUserId, Pageable pageable);

    TripDetails createTrip(UUID userId, TripCreateRequest request);

    TripDetails updateTrip(UUID tripId, UUID userId, TripUpdateRequest request);

    void deleteTrip(UUID tripId, UUID userId);

    void addStop(UUID tripId, UUID userId, AddressRequest address);

    void deleteStop(UUID stopId, UUID tripId, UUID userId);
}