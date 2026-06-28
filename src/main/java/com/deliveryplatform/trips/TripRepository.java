package com.deliveryplatform.trips;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    @EntityGraph(attributePaths = {"stops", "owner.profile.avatar"})
    Optional<Trip> findTripById(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"stops", "owner.profile.avatar"})
    List<Trip> findByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("""
            SELECT t.availableWeightKg - COALESCE(SUM(p.weightKg), 0)
            FROM Trip t
            LEFT JOIN t.bookings b
            LEFT JOIN b.parcel p
            WHERE t.id = :tripId
            GROUP BY t.id, t.availableWeightKg
    """)
    BigDecimal getRemainingWeight(@Param("tripId") UUID tripId);

}
