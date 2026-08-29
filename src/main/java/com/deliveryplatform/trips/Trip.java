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

    public void assertNotFull() {
        if (TripState.FULL.equals(this.state))
            throw new TripException(TripErrorCode.TRIP_FULL, "Trip is full");
    }

    public void assertInState(List<TripState> states, String message ) {
        if (!states.contains(this.state)) {
            throw new TripException(TripErrorCode.INVALID_STATE_TRANSITION, message);
        }
    }

    public void assertMaxTripDetourRequirement(Detour detour) {
        if (!isMaxDetourAccepted(BigDecimal.valueOf(detour.pickupDetourKm()), BigDecimal.valueOf(detour.dropoffDetourKm())))
            throw new TripException(TripErrorCode.MAX_DETOUR_EXCEEDED, "Trip max detour not satisfied");
    }

    public void assertTripCanReceiveRequests() {
        assertInState(List.of(TripState.PUBLISHED, TripState.ACTIVE), "Trip can not receive new requests");
        if (LocalDate.now().isAfter(this.departureDate)){
            throw new TripException(TripErrorCode.TRIP_DEPARTURE_PASSED, "Departure date passed");
        }

    }

    // ---- lifecycle -------------------------------------------------------

    public void update(UUID userId, Address departureAddress, Address arrivalAddress,
                       LocalDate departureDate, LocalDate arrivalDate,
                       BigDecimal availableWeightKg, Price pricePerKg,
                       BigDecimal maxDetourKm, String notes, boolean instantBooking) {
        assertOwnedBy(userId);
        assertInState(List.of(TripState.PUBLISHED, TripState.ACTIVE, TripState.FULL), "Can not update trip");

        boolean changesRoute = (departureAddress != null && !departureAddress.equals(this.departureAddress))
                || (arrivalAddress != null && !arrivalAddress.equals(this.arrivalAddress))
                || (departureDate != null && !departureDate.equals(this.departureDate))
                || (arrivalDate != null && !arrivalDate.equals(this.arrivalDate));

        if (changesRoute) {
            assertInState(List.of(TripState.PUBLISHED), "Trip route and dates can only be changed while published");
            this.departureAddress = departureAddress;
            this.arrivalAddress = arrivalAddress;
            this.departureDate = departureDate;
            this.arrivalDate = arrivalDate;
        }

        this.pricePerKg = pricePerKg;
        this.maxDetourKm = maxDetourKm;
        this.notes = notes;
        this.instantBooking = instantBooking;
        this.updateAvailableWeightKg(availableWeightKg);
    }

    public void cancel(UUID userId) {

        assertOwnedBy(userId);
        assertInState(List.of(TripState.PUBLISHED), "Trip already have related bookings");
        deleteAllStops();

        this.updateState(TripState.CANCELLED);
    }

    private void updateState(TripState newState) {
        if (newState.equals(this.state)) return;
        if (!isValidTransition(newState))
            throw new TripException(TripErrorCode.INVALID_STATE_TRANSITION,
                    "Cannot transition trip from %s to %s".formatted(this.state, newState));
        this.state = newState;
    }

    public void updateAvailableWeightKg(BigDecimal newAvailableWeightKg) {

        if (newAvailableWeightKg.compareTo(MIN_WEIGHT_KG) < 0)
            throw new TripException(TripErrorCode.WEIGHT_BELOW_MINIMUM,
                    "Available weight must not be under %s kg".formatted(MIN_WEIGHT_KG));

        BigDecimal alreadyReservedWeight = this.availableWeightKg.subtract(this.remainingWeightKg);
        if (newAvailableWeightKg.compareTo(alreadyReservedWeight) < 0)
            throw new TripException(TripErrorCode.WEIGHT_BELOW_RESERVED,
                    "Available weight must not be under already reserved weight of %s kg".formatted(alreadyReservedWeight));

        this.availableWeightKg = newAvailableWeightKg;
        this.remainingWeightKg = newAvailableWeightKg.subtract(alreadyReservedWeight);
        refreshCapacityState();
    }

    // ---- stops ------------------------------------------------------------

    public void addStop(UUID userId, Address address) {
        assertOwnedBy(userId);
        assertAddressUniquenessInTripOrThrow(address);
        var newStop = TripStop.create(address, this.stops.size() + 1);
        newStop.setTrip(this);
        this.stops.add(newStop);
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

    public void book(Booking newBooking) {
        assertTripCanReceiveRequests();
        newBooking.setTrip(this);
        this.bookings.add(newBooking);
        reserveWeight(newBooking.getBookingWeight());
    }

    public void unbook(Booking bookingToRemove) {
        this.bookings.remove(bookingToRemove);
        releaseWeight(bookingToRemove.getBookingWeight());
    }

    public void complete() {
        this.updateState(TripState.COMPLETED);
    }

    public void expire() {
        this.updateState(TripState.EXPIRED);
    }

    // ---- queries ------------------------------------------------------------

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

    private void deleteAllStops() {
        this.stops.forEach(TripStop::delete);
    }

    private void assertAddressUniquenessInTripOrThrow(Address addressToAdd) {
        var exists = this.getWayPoints().stream().anyMatch(address -> address.equals(addressToAdd));
        if (exists) throw new TripException(TripErrorCode.STOP_DUPLICATE_ADDRESS, "Stop address already exists in trip");
    }

    private void reserveWeight(BigDecimal weight) {
        if (weight.compareTo(this.remainingWeightKg) > 0)
            throw new TripException(TripErrorCode.WEIGHT_INSUFFICIENT_REMAINING,
                    "Not enough remaining weight: requested=%s, available=%s".formatted(weight, this.remainingWeightKg));
        this.remainingWeightKg = this.remainingWeightKg.subtract(weight);
        refreshCapacityState();
    }

    private void releaseWeight(BigDecimal weight) {
        this.remainingWeightKg = this.remainingWeightKg.add(weight);
        refreshCapacityState();
    }

    private void refreshCapacityState() {
        if (!List.of(TripState.PUBLISHED, TripState.ACTIVE, TripState.FULL).contains(this.state)) return;

        if (this.remainingWeightKg.compareTo(BigDecimal.ZERO) == 0) updateState(TripState.FULL);
        else if (this.remainingWeightKg.compareTo(this.availableWeightKg) == 0) updateState(TripState.PUBLISHED);
        else updateState(TripState.ACTIVE);
    }

    private boolean isValidTransition(TripState newState) {
        return switch (this.state) {
            case PUBLISHED -> newState == TripState.ACTIVE || newState == TripState.FULL
                    || newState == TripState.EXPIRED || newState == TripState.COMPLETED || newState == TripState.CANCELLED;
            case ACTIVE -> newState == TripState.PUBLISHED || newState == TripState.FULL
                    || newState == TripState.EXPIRED || newState == TripState.COMPLETED;
            case FULL -> newState == TripState.ACTIVE || newState == TripState.PUBLISHED
                    || newState == TripState.EXPIRED || newState == TripState.COMPLETED;
            case EXPIRED, COMPLETED, CANCELLED -> false;
        };
    }
}