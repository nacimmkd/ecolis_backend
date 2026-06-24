package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingStatus;
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
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted = false")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Embedded
    @AttributeOverride(name = "street",     column = @Column(name = "departure_street",      nullable = false, columnDefinition = "TEXT"))
    @AttributeOverride(name = "city",       column = @Column(name = "departure_city",        nullable = false, length = 100))
    @AttributeOverride(name = "postalCode", column = @Column(name = "departure_postal_code", nullable = false, length = 20))
    @AttributeOverride(name = "country",    column = @Column(name = "departure_country",     nullable = false, length = 60))
    @AttributeOverride(name = "latitude",   column = @Column(name = "departure_lat"))
    @AttributeOverride(name = "longitude",  column = @Column(name = "departure_lng"))
    private Address departureAddress;

    @Embedded
    @AttributeOverride(name = "street",     column = @Column(name = "arrival_street",      nullable = false, columnDefinition = "TEXT"))
    @AttributeOverride(name = "city",       column = @Column(name = "arrival_city",        nullable = false, length = 100))
    @AttributeOverride(name = "postalCode", column = @Column(name = "arrival_postal_code", nullable = false, length = 20))
    @AttributeOverride(name = "country",    column = @Column(name = "arrival_country",     nullable = false, length = 60))
    @AttributeOverride(name = "latitude",   column = @Column(name = "arrival_lat"))
    @AttributeOverride(name = "longitude",  column = @Column(name = "arrival_lng"))
    private Address arrivalAddress;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "available_weight_kg", precision = 8, scale = 2)
    private BigDecimal availableWeightKg;
    
    @Transient
    private BigDecimal remainingWeightKg;

    @Column(name = "price_per_kg", precision = 10, scale = 2)
    private BigDecimal pricePerKg;

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

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
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


    // methods ---------------------------------------------------------------------------------

    public void addStop(TripStop newStop) {
        newStop.setTrip(this);
        this.stops.add(newStop);
    }

    public void addStops(List<TripStop> newStops) {
        validateStopsSequence(newStops);
        newStops.forEach(this::addStop);
    }

    public void removeStop(TripStop stop) {
        this.stops.remove(stop);
    }

    public void removeAllStops() {
        this.stops.clear();
    }

    public void reorderStops() {
        for (int i = 0; i < stops.size(); i++) {
            stops.get(i).setOrder(i + 1);
        }
    }

    public void softDelete() {
        removeAllStops();
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now();
    }

    public BigDecimal getRemainingWeightKg() {
        if (availableWeightKg == null) return BigDecimal.ZERO;
        var usedWeight = bookings.stream()
                .filter(b -> !b.getStatus().equals(BookingStatus.CANCELLED))
                .map(b -> b.getParcel().getWeightKg())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return availableWeightKg.subtract(usedWeight);
    }

    private void validateStopsSequence(List<TripStop> newStops) {
        int offset = this.stops.size();
        for (int i = 0; i < newStops.size(); i++) {
            int expected = offset + i + 1;
            if (newStops.get(i).getOrder() != expected)
                throw new InvalidDomainStateException(
                        "Stop at index " + i + " must have order " + expected +
                                " but got " + newStops.get(i).getOrder()
                );
        }
    }
}