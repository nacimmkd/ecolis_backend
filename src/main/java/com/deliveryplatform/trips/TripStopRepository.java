package com.deliveryplatform.trips;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripStopRepository extends JpaRepository<TripStop, UUID> {

    int countByTripId(UUID tripId);
    List<TripStop> findByTripIdOrderByStopOrder(UUID tripId);

}
