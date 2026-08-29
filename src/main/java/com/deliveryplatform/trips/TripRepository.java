package com.deliveryplatform.trips;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    @EntityGraph(attributePaths = {"stops"})
    Optional<Trip> findTripById(UUID id);

    @EntityGraph(attributePaths = {"stops"})
    Page<Trip> findTripByOwner_IdAndStateNot(UUID ownerId, TripState excludedState, Pageable pageable);

    @EntityGraph(attributePaths = {"stops"})
    Page<Trip> findTripByOwner_IdAndState(UUID ownerId, TripState state, Pageable pageable);

    @EntityGraph(attributePaths = {"stops"})
    Page<Trip> findAll(Pageable pageable);

    long countByOwner_IdAndState(UUID ownerId, TripState state);

    @EntityGraph(attributePaths = {"stops", "owner.profile"})
    @Query("""
        SELECT t FROM Trip t
        WHERE t.state IN :states
          AND t.departureDate = :departureDate
          AND t.remainingWeightKg >= :weightKg
          AND t.departureAddress.latitude  BETWEEN :minLat AND :maxLat
          AND t.departureAddress.longitude BETWEEN :minLng AND :maxLng
    """)
    List<Trip> findCandidateTrips(
            @Param("states")        List<TripState> states,
            @Param("departureDate") LocalDate  departureDate,
            @Param("weightKg")      BigDecimal weightKg,
            @Param("minLat")        double     minLat,
            @Param("maxLat")        double     maxLat,
            @Param("minLng")        double     minLng,
            @Param("maxLng")        double     maxLng
    );

    @EntityGraph(attributePaths = {"stops"})
    List<Trip> findByStateInAndDepartureDateBefore(List<TripState> states, LocalDate date);

    @Query("""
        SELECT COUNT(t) > 0 FROM Trip t
        WHERE t.owner.id = :ownerId
          AND t.state IN :states
          AND t.departureDate = :departureDate
          AND t.arrivalDate = :arrivalDate
          AND t.departureAddress.street = :departureStreet
          AND t.departureAddress.city = :departureCity
          AND t.departureAddress.postalCode = :departurePostalCode
          AND t.departureAddress.country = :departureCountry
          AND t.arrivalAddress.street = :arrivalStreet
          AND t.arrivalAddress.city = :arrivalCity
          AND t.arrivalAddress.postalCode = :arrivalPostalCode
          AND t.arrivalAddress.country = :arrivalCountry
    """)
    boolean existsDuplicatePublishedTrip(
            @Param("ownerId")             UUID            ownerId,
            @Param("states")              List<TripState> states,
            @Param("departureDate")       LocalDate  departureDate,
            @Param("arrivalDate")         LocalDate  arrivalDate,
            @Param("departureStreet")     String     departureStreet,
            @Param("departureCity")       String     departureCity,
            @Param("departurePostalCode") String     departurePostalCode,
            @Param("departureCountry")    String     departureCountry,
            @Param("arrivalStreet")       String     arrivalStreet,
            @Param("arrivalCity")         String     arrivalCity,
            @Param("arrivalPostalCode")   String     arrivalPostalCode,
            @Param("arrivalCountry")      String     arrivalCountry
    );

}
