package com.deliveryplatform.bookings;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.parcel p JOIN FETCH p.owner JOIN FETCH b.trip t JOIN FETCH t.owner WHERE b.id = :bookingId ")
    Optional<Booking> findBookingWithParticipants(@Param("bookingId") UUID bookingId);
}
