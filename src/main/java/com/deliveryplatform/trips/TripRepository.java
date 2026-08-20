package com.deliveryplatform.trips;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    @EntityGraph(attributePaths = {"stops"})
    Optional<Trip> findTripById(UUID id);

    @EntityGraph(attributePaths = {"stops"})
    Page<Trip> findTripByOwner_Id(UUID ownerId, Pageable pageable);

    @EntityGraph(attributePaths = {"stops"})
    Page<Trip> findTripByOwner_IdAndState(UUID ownerId, TripState state, Pageable pageable);

    @EntityGraph(attributePaths = {"stops"})
    Page<Trip> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"stops", "owner.profile"})
    @Query("""
        SELECT t FROM Trip t
        WHERE t.state = :state
          AND t.departureDate = :departureDate
          AND t.remainingWeightKg >= :weightKg
          AND t.departureAddress.latitude  BETWEEN :minLat AND :maxLat
          AND t.departureAddress.longitude BETWEEN :minLng AND :maxLng
    """)
    List<Trip> findCandidateTrips(
            @Param("state")         TripState  state,
            @Param("departureDate") LocalDate  departureDate,
            @Param("weightKg")      BigDecimal weightKg,
            @Param("minLat")        double     minLat,
            @Param("maxLat")        double     maxLat,
            @Param("minLng")        double     minLng,
            @Param("maxLng")        double     maxLng
    );

}
