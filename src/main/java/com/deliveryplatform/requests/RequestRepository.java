package com.deliveryplatform.requests;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile", "trip.stops",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    Optional<Request> findRequestById(UUID id);

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    @Query("""
            SELECT r FROM Request r
            WHERE r.parcel.owner.id = :userId
            ORDER BY r.requestedAt DESC
            """)
    List<Request> findSentRequestsByUserId(@Param("userId") UUID userId);

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    @Query("""
            SELECT r FROM Request r
            WHERE r.trip.owner.id = :userId
            ORDER BY r.requestedAt DESC
            """)
    List<Request> findReceivedRequestsByUserId(@Param("userId") UUID userId);

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    @Query("""
            SELECT r FROM Request r
            WHERE r.parcel.id = :parcelId
            ORDER BY r.requestedAt DESC
            """)
    List<Request> findByParcelId(@Param("parcelId") UUID parcelId);

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    @Query("""
            SELECT r FROM Request r
            WHERE r.trip.id = :tripId
            ORDER BY r.requestedAt DESC
            """)
    List<Request> findByTripId(@Param("tripId") UUID tripId);

    @EntityGraph(attributePaths = {
            "trip", "trip.owner",
            "parcel", "parcel.owner"
    })
    List<Request> findByStateAndRequestedAtBefore(RequestState status, OffsetDateTime cutoff);

    boolean existsByParcelIdAndTripId(UUID parcelId, UUID tripId);

    long countByTripId(UUID tripId);
}