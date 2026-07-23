package com.deliveryplatform.bookings;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @EntityGraph(attributePaths = {"parcel", "parcel.owner", "trip", "trip.owner"})
    Optional<Booking> findBookingById(UUID bookingId);

    @EntityGraph(attributePaths = {"parcel", "parcel.owner", "parcel.owner.profile"})
    List<Booking> findByTripIdOrderByCreatedAtDesc(UUID tripId);

    @EntityGraph(attributePaths = {"parcel", "trip", "trip.owner", "trip.owner.profile"})
    List<Booking> findByParcelIdOrderByCreatedAtDesc(UUID parcelId);

    long countByTripId(UUID tripId);

    long countByParcelId(UUID parcelId);
}