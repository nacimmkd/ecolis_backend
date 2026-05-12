package com.deliveryplatform.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {

    boolean existsByParcelIdAndTripId(UUID parcelId, UUID tripId);

    @Query("""
            SELECT r FROM Request r
            LEFT JOIN FETCH r.trip t
            LEFT JOIN FETCH r.parcel p
            LEFT JOIN FETCH t.owner carrier
            LEFT JOIN FETCH p.owner sender
            WHERE sender.id = :userId
            ORDER BY r.requestedAt DESC
            """)
    List<Request> findSentRequestsByUserId(@Param("userId") UUID userId);


    @Query("""
            SELECT r FROM Request r
            LEFT JOIN FETCH r.trip t
            LEFT JOIN FETCH r.parcel p
            LEFT JOIN FETCH t.owner carrier
            LEFT JOIN FETCH p.owner sender
            WHERE carrier.id = :userId
            ORDER BY r.requestedAt DESC
            """)
    List<Request> findReceivedRequestsByUserId(@Param("userId") UUID userId);


    @Query("""
            SELECT r FROM Request r
            LEFT JOIN FETCH r.trip t
            LEFT JOIN FETCH r.parcel p
            LEFT JOIN FETCH t.owner
            LEFT JOIN FETCH p.owner
            WHERE p.id = :parcelId
            ORDER BY r.requestedAt DESC
            """)
    List<Request> findByParcelId(@Param("parcelId") UUID parcelId);


    @Query("""
            SELECT r FROM Request r
            LEFT JOIN FETCH r.trip t
            LEFT JOIN FETCH r.parcel p
            LEFT JOIN FETCH t.owner
            LEFT JOIN FETCH p.owner
            WHERE t.id = :tripId
            ORDER BY r.requestedAt DESC
            """)
    List<Request> findByTripId(@Param("tripId") UUID tripId);


    @Query("""
       SELECT r FROM Request r
       LEFT JOIN FETCH r.trip t
       LEFT JOIN FETCH r.parcel p
       LEFT JOIN FETCH t.owner carrier
       LEFT JOIN FETCH p.owner sender
       WHERE sender.id = :userId
       OR carrier.id = :userId
       ORDER BY r.requestedAt DESC
       """)
    List<Request> findAllByInvolvedUser(@Param("userId") UUID userId);
}

