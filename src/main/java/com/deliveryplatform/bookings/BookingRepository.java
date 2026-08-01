package com.deliveryplatform.bookings;

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
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile", "trip.stops",
            "parcel", "parcel.owner", "parcel.owner.profile", "payment"
    })
    Optional<Booking> findBookingById(UUID bookingId);

    @EntityGraph(attributePaths = {"parcel", "parcel.owner", "parcel.owner.profile"})
    List<Booking> findByTripIdAndStateInOrderByCreatedAtDesc(UUID trip_id, List<BookingState> state);

    @EntityGraph(attributePaths = {"parcel", "trip", "trip.owner", "trip.owner.profile"})
    List<Booking> findByParcelIdOrderByCreatedAtDesc(UUID parcelId);

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    @Query("""
            SELECT b FROM Booking b
            WHERE b.parcel.owner.id = :userId
            ORDER BY b.createdAt DESC
            """)
    List<Booking> findSentBookingsByUserId(@Param("userId") UUID userId);

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    @Query("""
            SELECT b FROM Booking b
            WHERE b.trip.owner.id = :userId
            ORDER BY b.createdAt DESC
            """)
    List<Booking> findReceivedBookingsByUserId(@Param("userId") UUID userId);

    @EntityGraph(attributePaths = {"trip", "trip.owner", "parcel", "parcel.owner"})
    List<Booking> findByStateAndCreatedAtBefore(BookingState state, OffsetDateTime cutoff);

    boolean existsByTripIdAndParcelIdAndStateIn(UUID tripId, UUID parcelId, List<BookingState> states);

    long countByTripId(UUID tripId);

    long countByParcelId(UUID parcelId);
}
