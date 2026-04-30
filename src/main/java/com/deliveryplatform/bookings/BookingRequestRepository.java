package com.deliveryplatform.bookings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRequestRepository extends JpaRepository<BookingRequest, UUID> {

    boolean existsByParcelIdAndTripId(UUID parcelId, UUID tripId);

    @Query("""
            SELECT br FROM BookingRequest br
            LEFT JOIN FETCH br.trip t
            LEFT JOIN FETCH br.parcel p
            LEFT JOIN FETCH t.owner
            LEFT JOIN FETCH p.owner
            WHERE t.id = :tripId
            ORDER BY br.requestedAt DESC
            """)
    List<BookingRequest> findByTripId(@Param("tripId") UUID tripId);


    @Query("""
            SELECT br FROM BookingRequest br
            LEFT JOIN FETCH br.trip t
            LEFT JOIN FETCH br.parcel p
            LEFT JOIN FETCH t.owner
            LEFT JOIN FETCH p.owner
            WHERE p.id = :parcelId
            ORDER BY br.requestedAt DESC
            """)
    List<BookingRequest> findByParcelId(@Param("parcelId") UUID parcelId);
}

