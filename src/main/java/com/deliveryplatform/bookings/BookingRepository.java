package com.deliveryplatform.bookings;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.parcel p JOIN FETCH p.owner JOIN FETCH b.trip t JOIN FETCH t.owner WHERE b.id = :bookingId ")
    Optional<Booking> findBookingById(@Param("bookingId") UUID bookingId);

    List<Booking> findByTripId(UUID tripId);

    Booking findByParcelId(UUID parcelId);

    @Query("""
       SELECT b FROM Booking b
       WHERE b.trip.owner.id = :userId
       OR b.parcel.owner.id = :userId
       ORDER BY b.createdAt DESC
       """)
    List<Booking> findAllByInvolvedUser(@Param("userId") UUID userId);


}
