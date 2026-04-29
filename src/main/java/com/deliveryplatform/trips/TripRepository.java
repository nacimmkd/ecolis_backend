package com.deliveryplatform.trips;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {


    @Query("""
            SELECT t FROM Trip t
            LEFT JOIN FETCH t.stops
            LEFT JOIN FETCH t.owner u
            LEFT JOIN FETCH u.profile p
            LEFT JOIN FETCH p.avatar
            WHERE t.id = :id
            """)
    Optional<Trip> findByIdWithStopsAndOwner(@Param("id") UUID id);



    @Query("""
            SELECT t FROM Trip t
            LEFT JOIN FETCH t.stops
            LEFT JOIN FETCH t.owner u
            LEFT JOIN FETCH u.profile p
            LEFT JOIN FETCH p.avatar
            WHERE u.id = :userId
            """)
    List<Trip> findByOwnerId(UUID userId);
}
