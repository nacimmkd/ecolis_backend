package com.deliveryplatform.trips;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {


    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.stops WHERE t.id = :id")
    Optional<Trip> findByIdWithStops(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"stops"})
    List<Trip> findByUserId(UUID userId);

    @EntityGraph(attributePaths = {"stops"})
    List<Trip> findAllTripsByStatus(TripStatus status);
}
