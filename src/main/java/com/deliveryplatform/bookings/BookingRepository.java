package com.deliveryplatform.bookings;

import com.deliveryplatform.trips.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile", "trip.stops",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    Optional<Booking> findBookingById(UUID bookingId);

    @EntityGraph(attributePaths = {"parcel", "parcel.owner", "parcel.owner.profile"})
    Page<Booking> findByTripIdAndStateIn(UUID trip_id, List<BookingState> state, Pageable pageable);

    @EntityGraph(attributePaths = {"parcel", "trip", "trip.owner", "trip.owner.profile"})
    Page<Booking> findByParcelIdOrderByCreatedAtDesc(UUID parcelId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    @Query("""
            SELECT b FROM Booking b
            WHERE b.parcel.owner.id = :userId
            """)
    Page<Booking> findSentBookingsByUserId(@Param("userId") UUID userId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    @Query("""
            SELECT b FROM Booking b
            WHERE b.trip.owner.id = :userId
            """)
    Page<Booking> findReceivedBookingsByUserId(@Param("userId") UUID userId, Pageable pageable);

    @EntityGraph(attributePaths = {"trip", "trip.owner", "parcel", "parcel.owner"})
    List<Booking> findByStateAndCreatedAtBefore(BookingState state, OffsetDateTime cutoff);

    @EntityGraph(attributePaths = {
            "trip", "trip.owner", "trip.owner.profile", "trip.stops",
            "parcel", "parcel.owner", "parcel.owner.profile"
    })
    Optional<Booking> findByTripIdAndParcelIdAndStateIn(UUID tripId, UUID parcelId, List<BookingState> states);

    long countByTripIdAndStateIn(UUID tripId, Collection<BookingState> states);

    boolean existsByTripIdAndStateIn(UUID tripId, Collection<BookingState> states);

    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.state IN :states
              AND ( (b.parcel.owner.id = :userIdA AND b.trip.owner.id = :userIdB)
                    OR (b.parcel.owner.id = :userIdB AND b.trip.owner.id = :userIdA))
        """)
    boolean existsBookingBetweenUsers(@Param("userIdA") UUID userIdA, @Param("userIdB") UUID userIdB, @Param("states") Collection<BookingState> states);
}
