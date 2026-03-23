package com.deliveryplatform.trips;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    @EntityGraph(attributePaths = {"stops"})
    List<Trip> findByUserId(UUID userId);

    @EntityGraph(attributePaths = {"stops"})
    List<Trip> findAllTripsByStatus(TripStatus status);
}
