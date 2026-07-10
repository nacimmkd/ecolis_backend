package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted = false")
public class Trip {

    public static BigDecimal MIN_WEIGHT_KG = BigDecimal.ONE;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Setter
    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "departure_street", nullable = false, columnDefinition = "TEXT"))
    @AttributeOverride(name = "city", column = @Column(name = "departure_city", nullable = false, length = 100))
    @AttributeOverride(name = "postalCode", column = @Column(name = "departure_postal_code", nullable = false, length = 20))
    @AttributeOverride(name = "country", column = @Column(name = "departure_country", nullable = false, length = 60))
    @AttributeOverride(name = "latitude", column = @Column(name = "departure_lat"))
    @AttributeOverride(name = "longitude", column = @Column(name = "departure_lng"))
    private Address departureAddress;

    @Setter
    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "arrival_street", nullable = false, columnDefinition = "TEXT"))
    @AttributeOverride(name = "city", column = @Column(name = "arrival_city", nullable = false, length = 100))
    @AttributeOverride(name = "postalCode", column = @Column(name = "arrival_postal_code", nullable = false, length = 20))
    @AttributeOverride(name = "country", column = @Column(name = "arrival_country", nullable = false, length = 60))
    @AttributeOverride(name = "latitude", column = @Column(name = "arrival_lat"))
    @AttributeOverride(name = "longitude", column = @Column(name = "arrival_lng"))
    private Address arrivalAddress;

    @Setter
    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Setter
    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "available_weight_kg", precision = 8, scale = 2)
    private BigDecimal availableWeightKg;

    @Column(name = "remaining_weight_kg", precision = 8, scale = 2)
    private BigDecimal remainingWeightKg;

    @Setter
    @Column(name = "price_per_kg", precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    @Setter
    @Column(name = "instant_booking")
    private boolean instantBooking;

    @Setter
    @Column(name = "max_detour_km", nullable = false, precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal maxDetourKm = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TripState state = TripState.PUBLISHED;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Setter
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    @Builder.Default
    private List<TripStop> stops = new ArrayList<>();

    @OneToMany(mappedBy = "trip")
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @Setter
    @Builder.Default
    private boolean deleted = false;

    @Setter
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;


    @PrePersist
    public void prePersist() {
        if (this.remainingWeightKg == null) {
            this.remainingWeightKg = this.availableWeightKg;
        }
    }

    // trips ---------------------------------------------------------------------------------

    public void softDelete() {
        removeAllStops();
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now();
    }

    public void updateState(TripState newState) {
        if (newState.equals(this.state)) return;
        assertTripNotDeleted();
        if (!isValidTransition(newState))
            throw new InvalidDomainStateException("Invalid state transition: Cannot transition trip from %s to %s".formatted(this.state, newState));
        this.state = newState;
    }

    public UUID getOwnerId() {
        return this.owner.getId();
    }

    public boolean isMaxDetourAccepted(BigDecimal detour) {
        return this.maxDetourKm.compareTo(detour) >= 0;
    }

    public void updateAvailableWeightKg(BigDecimal newAvailableWeightKg) {
        if (availableWeightKg.compareTo(MIN_WEIGHT_KG) < 0)
            throw new InvalidDomainStateException("available weight must not be under %s kg".formatted(MIN_WEIGHT_KG));

        BigDecimal difference = newAvailableWeightKg.subtract(this.availableWeightKg);

        if (difference.compareTo(BigDecimal.ZERO) == 0) return; // nothing to change

        // apply changes
        if (difference.compareTo(BigDecimal.ZERO) > 0) releaseWeight(difference);
        else reserveWeight(difference.abs());

        this.availableWeightKg = newAvailableWeightKg;
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

    // stops ---------------------------------------------------------------------------------
    public void addStop(Address address) {
        assertAddressUniquenessInTripOrThrow(address);
        var newStop = TripStop.create(
                address,
                this.stops.size() + 1
        );
        newStop.setTrip(this);
        this.stops.add(newStop);
    }

    public void addStops(List<TripStop> newStops) {
        assertStopUniquenessInTripOrThrow(newStops);
        validateStopsSequence(newStops);
        newStops.forEach(stop -> stop.setTrip(this));
        this.stops.addAll(newStops);
    }

    public void updateStops(List<TripStop> newStops) {
        if (newStops == null) return;
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
    }

    public void removeStopAndReorder(TripStop stop) {
        this.stops.remove(stop);
        TripStop.reorderStops(this.stops);
    }

    private void removeStops(List<TripStop> stopsToRemove) {
        this.stops.removeAll(stopsToRemove);
    }

    private void removeAllStops() {
        this.stops.clear();
    }


    // bookings ---------------------------------------------------------------------------------

    public void addBooking(Booking newBooking) {
        assertTripNotDeleted();
        newBooking.setTrip(this);
        this.bookings.add(newBooking);
        reserveWeight(newBooking.getBookingWeight());
    }

    public void removeBooking(Booking bookingToRemove) {
        bookingToRemove.setTrip(null);
        this.bookings.remove(bookingToRemove);
        releaseWeight(bookingToRemove.getBookingWeight());
    }

    // private ---------------------------------------------------------------------------------

    private void validateStopsSequence(List<TripStop> newStops) {
        int offset = this.stops.size();
        for (int i = 0; i < newStops.size(); i++) {
            int expected = offset + i + 1;
            if (newStops.get(i).getOrder() != expected)
                throw new InvalidDomainStateException("Stop at index %s must have order %s but got %s".formatted(i, expected, newStops.get(i).getOrder()));
        }
    }

    private void assertTripNotDeleted() {
        if (this.deleted) throw new InvalidDomainStateException("action can not be performed because trip is deleted");
    }

    private void assertAddressUniquenessInTripOrThrow(Address addressToAdd) {
        var exists = this.getWayPoints().stream()
                .anyMatch(address -> address.equals(addressToAdd));
        if (exists) {
            throw new InvalidDomainStateException("way point already in trip");
        }
    }

    private void assertStopUniquenessInTripOrThrow(List<TripStop> stopsToAdd) {
        var existingAddresses = new HashSet<>(this.getWayPoints());

        for (TripStop stop : stopsToAdd) {
            if (existingAddresses.contains(stop.getAddress())) {
                throw new InvalidDomainStateException("way point already in trip: " + stop.getAddress());
            }
        }
    }


    private void reserveWeight(BigDecimal weight) {
        assertTripNotDeleted();

        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidDomainStateException("Weight must be positive");

        if (weight.compareTo(this.remainingWeightKg) > 0)
            throw new InvalidDomainStateException("Not enough remaining weight: requested=%s, available=%s".formatted(weight, this.remainingWeightKg));
        this.remainingWeightKg = this.remainingWeightKg.subtract(weight);

        // update state
        if (this.remainingWeightKg.compareTo(BigDecimal.ZERO) == 0) {
            updateState(TripState.FULL);
        }
    }

    private void releaseWeight(BigDecimal weight) {

        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDomainStateException("Weight must be positive");
        }
        this.remainingWeightKg = this.remainingWeightKg.add(weight);
        // update state
        if (this.state == TripState.FULL) {
            updateState(TripState.PUBLISHED);
        }
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