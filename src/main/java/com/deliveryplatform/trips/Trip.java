package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.payments.Price;
import com.deliveryplatform.matching.Detour;
import com.deliveryplatform.trips.dto.TripCreateRequest;
import com.deliveryplatform.trips.exceptions.TripErrorCode;
import com.deliveryplatform.trips.exceptions.TripException;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "trips")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLRestriction("deleted = false")
public class Trip {

    public static BigDecimal MIN_WEIGHT_KG = BigDecimal.ONE;
    public static BigDecimal MIN_PRICE_KG = BigDecimal.valueOf(0.1);
    public static BigDecimal MIN_DETOUR_KG = BigDecimal.ONE;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "departure_street", nullable = false, columnDefinition = "TEXT"))
    @AttributeOverride(name = "city", column = @Column(name = "departure_city", nullable = false, length = 100))
    @AttributeOverride(name = "postalCode", column = @Column(name = "departure_postal_code", nullable = false, length = 20))
    @AttributeOverride(name = "country", column = @Column(name = "departure_country", nullable = false, length = 60))
    @AttributeOverride(name = "latitude", column = @Column(name = "departure_lat"))
    @AttributeOverride(name = "longitude", column = @Column(name = "departure_lng"))
    private Address departureAddress;

    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "arrival_street", nullable = false, columnDefinition = "TEXT"))
    @AttributeOverride(name = "city", column = @Column(name = "arrival_city", nullable = false, length = 100))
    @AttributeOverride(name = "postalCode", column = @Column(name = "arrival_postal_code", nullable = false, length = 20))
    @AttributeOverride(name = "country", column = @Column(name = "arrival_country", nullable = false, length = 60))
    @AttributeOverride(name = "latitude", column = @Column(name = "arrival_lat"))
    @AttributeOverride(name = "longitude", column = @Column(name = "arrival_lng"))
    private Address arrivalAddress;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "available_weight_kg", precision = 8, scale = 2)
    private BigDecimal availableWeightKg;

    @Column(name = "remaining_weight_kg", precision = 8, scale = 2)
    private BigDecimal remainingWeightKg;

    @Embedded
    @AttributeOverride(name = "amountInCents", column = @Column(name = "price_per_kg_amount_in_cents", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "price_per_kg_currency", nullable = false, length = 3))
    private Price pricePerKg;

    @Column(name = "instant_booking")
    private boolean instantBooking;

    @Column(name = "max_detour_km", nullable = false, precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal maxDetourKm = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TripState state = TripState.PUBLISHED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    @OrderBy("order ASC")
    @Builder.Default
    private List<TripStop> stops = new ArrayList<>();

    @OneToMany(mappedBy = "trip")
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        if (this.remainingWeightKg == null) {
            this.remainingWeightKg = this.availableWeightKg;
        }
    }

    // ---- factory ------------------------------------------------------------

    public static Trip createFromRequest(TripCreateRequest request, Address departureAddress, Address arrivalAddress, User owner) {
        BigDecimal weight = request.availableWeightKg();
        return Trip.builder()
                .owner(owner)
                .departureAddress(departureAddress)
                .arrivalAddress(arrivalAddress)
                .departureDate(request.departureDate())
                .arrivalDate(request.arrivalDate())
                .availableWeightKg(weight)
                .remainingWeightKg(weight)
                .pricePerKg(request.pricePerKg())
                .maxDetourKm(request.maxDetourKm())
                .instantBooking(request.instantBooking())
                .notes(request.notes())
                .build();
    }

    // ---- invariants / guards ------------------------------------------------

    public void assertOwnedBy(UUID userId) {
        if (!this.owner.getId().equals(userId))
            throw new TripException(TripErrorCode.TRIP_NOT_OWNED, "User is not the owner of this trip");
    }

    public void assertIsPublished() {
        if (!TripState.PUBLISHED.equals(this.state))
            throw new TripException(TripErrorCode.TRIP_NOT_PUBLISHED, "Trip is not published");
    }

    public void assertNotDeleted() {
        if (this.deleted)
            throw new TripException(TripErrorCode.TRIP_DELETED, "Action cannot be performed because trip is deleted");
    }

    public void assertNotFull() {
        if (TripState.FULL.equals(this.state))
            throw new TripException(TripErrorCode.TRIP_FULL, "Trip is full");
    }

    public void assertMaxTripDetourRequirement(Detour detour) {
        if (!isMaxDetourAccepted(BigDecimal.valueOf(detour.pickupDetourKm()), BigDecimal.valueOf(detour.dropoffDetourKm())))
            throw new TripException(TripErrorCode.MAX_DETOUR_EXCEEDED, "Trip max detour not satisfied");
    }

    // ---- lifecycle -------------------------------------------------------

    public void update(UUID userId, Address departureAddress, Address arrivalAddress,
                       LocalDate departureDate, LocalDate arrivalDate,
                       BigDecimal availableWeightKg, Price pricePerKg,
                       BigDecimal maxDetourKm, String notes, boolean instantBooking) {
        this.assertOwnedBy(userId);
        this.assertIsPublished();

        this.departureAddress = departureAddress;
        this.arrivalAddress = arrivalAddress;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.pricePerKg = pricePerKg;
        this.maxDetourKm = maxDetourKm;
        this.notes = notes;
        this.instantBooking = instantBooking;
        this.updateAvailableWeightKg(availableWeightKg);
    }

    public void delete(UUID userId) {
        assertOwnedBy(userId);
        assertIsPublished();
        deleteAllStops();
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now();
    }

    public void updateState(TripState newState) {
        if (newState.equals(this.state)) return;
        assertNotDeleted();
        if (!isValidTransition(newState))
            throw new TripException(TripErrorCode.INVALID_STATE_TRANSITION,
                    "Cannot transition trip from %s to %s".formatted(this.state, newState));
        this.state = newState;
    }

    public void updateAvailableWeightKg(BigDecimal newAvailableWeightKg) {
        assertNotDeleted();

        if (newAvailableWeightKg.compareTo(MIN_WEIGHT_KG) < 0)
            throw new TripException(TripErrorCode.WEIGHT_BELOW_MINIMUM,
                    "Available weight must not be under %s kg".formatted(MIN_WEIGHT_KG));

        BigDecimal alreadyReservedWeight = this.availableWeightKg.subtract(this.remainingWeightKg);
        if (newAvailableWeightKg.compareTo(alreadyReservedWeight) < 0)
            throw new TripException(TripErrorCode.WEIGHT_BELOW_RESERVED,
                    "Available weight must not be under already reserved weight of %s kg".formatted(alreadyReservedWeight));

        BigDecimal difference = newAvailableWeightKg.subtract(this.availableWeightKg);
        if (difference.compareTo(BigDecimal.ZERO) == 0) return;

        if (difference.compareTo(BigDecimal.ZERO) > 0) releaseWeight(difference);
        else reserveWeight(difference.abs());

        this.availableWeightKg = newAvailableWeightKg;
    }

    // ---- stops ------------------------------------------------------------

    public void addStop(UUID userId, Address address) {
        assertOwnedBy(userId);
        assertAddressUniquenessInTripOrThrow(address);
        var newStop = TripStop.create(address, this.stops.size() + 1);
        newStop.setTrip(this);
        this.stops.add(newStop);
    }

    public List<TripStop> updateStops(UUID userId, List<TripStop> newStops) {
        assertOwnedBy(userId);
        if (newStops == null) return this.stops;

        if (newStops.isEmpty()) {
            this.stops.clear();
        } else {
            var toDelete = this.stops.stream()
                    .filter(stop -> !newStops.contains(stop))
                    .toList();
            removeStops(toDelete);
            addStops(newStops);
            TripStop.reorderStops(this.stops);
        }
        return this.stops;
    }

    public void removeStop(UUID userId, UUID stopId) {
        assertOwnedBy(userId);
        var stop = getStopById(stopId);
        stop.delete();
        this.stops.remove(stop);
        TripStop.reorderStops(this.stops);
    }

    public TripStop getStopById(UUID stopId) {
        return this.stops.stream()
                .filter(s -> s.getId().equals(stopId))
                .findFirst()
                .orElseThrow(() -> new TripException(TripErrorCode.STOP_NOT_FOUND, "Trip stop not found"));
    }

    // ---- bookings ------------------------------------------------------------

    public void reserveBooking(Booking newBooking) {
        assertNotDeleted();
        newBooking.setTrip(this);
        this.bookings.add(newBooking);
        reserveWeight(newBooking.getBookingWeight());
    }

    public void removeBooking(Booking bookingToRemove) {
        bookingToRemove.setTrip(null);
        this.bookings.remove(bookingToRemove);
        releaseWeight(bookingToRemove.getBookingWeight());
    }

    // ---- queries ------------------------------------------------------------

    public UUID getOwnerId() {
        return this.owner.getId();
    }

    public boolean isMaxDetourAccepted(BigDecimal pickUpDetour, BigDecimal dropOffDetour) {
        return this.maxDetourKm.compareTo(pickUpDetour) >= 0
                && this.maxDetourKm.compareTo(dropOffDetour) >= 0;
    }

    public List<Address> getWayPoints() {
        var wayPoints = new ArrayList<Address>();
        wayPoints.add(departureAddress);
        this.stops.stream()
                .sorted(Comparator.comparingInt(TripStop::getOrder))
                .map(TripStop::getAddress)
                .forEach(wayPoints::add);
        wayPoints.add(arrivalAddress);
        return wayPoints;
    }

    // ---- private helpers ------------------------------------------------------------

    private void addStops(List<TripStop> newStops) {
        assertStopUniquenessInTripOrThrow(newStops);
        validateStopsSequence(newStops);
        newStops.forEach(stop -> stop.setTrip(this));
        this.stops.addAll(newStops);
    }

    private void removeStops(List<TripStop> stopsToRemove) {
        stopsToRemove.forEach(TripStop::delete);
        this.stops.removeAll(stopsToRemove);
    }

    private void deleteAllStops() {
        this.stops.forEach(TripStop::delete);
    }

    private void validateStopsSequence(List<TripStop> newStops) {
        int offset = this.stops.size();
        for (int i = 0; i < newStops.size(); i++) {
            int expected = offset + i + 1;
            if (newStops.get(i).getOrder() != expected)
                throw new TripException(TripErrorCode.STOP_SEQUENCE_INVALID,
                        "Stop at index %s must have order %s but got %s".formatted(i, expected, newStops.get(i).getOrder()));
        }
    }

    private void assertAddressUniquenessInTripOrThrow(Address addressToAdd) {
        var exists = this.getWayPoints().stream().anyMatch(address -> address.equals(addressToAdd));
        if (exists) throw new TripException(TripErrorCode.STOP_DUPLICATE_ADDRESS, "Stop address already exists in trip");
    }

    private void assertStopUniquenessInTripOrThrow(List<TripStop> stopsToAdd) {
        var existingAddresses = new HashSet<>(this.getWayPoints());
        for (TripStop stop : stopsToAdd) {
            if (existingAddresses.contains(stop.getAddress()))
                throw new TripException(TripErrorCode.STOP_DUPLICATE_ADDRESS,
                        "Way point already in trip: " + stop.getAddress());
        }
    }

    private void reserveWeight(BigDecimal weight) {
        assertNotDeleted();
        if (weight.compareTo(this.remainingWeightKg) > 0)
            throw new TripException(TripErrorCode.WEIGHT_INSUFFICIENT_REMAINING,
                    "Not enough remaining weight: requested=%s, available=%s".formatted(weight, this.remainingWeightKg));
        this.remainingWeightKg = this.remainingWeightKg.subtract(weight);
        if (this.remainingWeightKg.compareTo(BigDecimal.ZERO) == 0) updateState(TripState.FULL);
    }

    private void releaseWeight(BigDecimal weight) {
        this.remainingWeightKg = this.remainingWeightKg.add(weight);
        if (this.state == TripState.FULL) updateState(TripState.PUBLISHED);
    }

    private boolean isValidTransition(TripState newState) {
        return switch (this.state) {
            case PUBLISHED -> newState == TripState.IN_TRANSIT || newState == TripState.CANCELLED;
            case FULL -> newState == TripState.PUBLISHED || newState == TripState.IN_TRANSIT;
            case IN_TRANSIT -> newState == TripState.COMPLETED || newState == TripState.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}