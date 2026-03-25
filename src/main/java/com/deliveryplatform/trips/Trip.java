package com.deliveryplatform.trips;

import com.deliveryplatform.common.addresses.GeoAddress;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "trips")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",     column = @Column(name = "departure_street",      nullable = false, columnDefinition = "TEXT")),
            @AttributeOverride(name = "city",       column = @Column(name = "departure_city",        nullable = false, length = 100)),
            @AttributeOverride(name = "postalCode", column = @Column(name = "departure_postal_code", nullable = false, length = 20)),
            @AttributeOverride(name = "country",    column = @Column(name = "departure_country",     nullable = false, length = 60)),
            @AttributeOverride(name = "latitude",   column = @Column(name = "departure_lat",         precision = 10, scale = 7)),
            @AttributeOverride(name = "longitude",  column = @Column(name = "departure_lng",         precision = 10, scale = 7))
    })
    private GeoAddress departure;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",     column = @Column(name = "arrival_street",      nullable = false, columnDefinition = "TEXT")),
            @AttributeOverride(name = "city",       column = @Column(name = "arrival_city",        nullable = false, length = 100)),
            @AttributeOverride(name = "postalCode", column = @Column(name = "arrival_postal_code", nullable = false, length = 20)),
            @AttributeOverride(name = "country",    column = @Column(name = "arrival_country",     nullable = false, length = 60)),
            @AttributeOverride(name = "latitude",   column = @Column(name = "arrival_lat",         precision = 10, scale = 7)),
            @AttributeOverride(name = "longitude",  column = @Column(name = "arrival_lng",         precision = 10, scale = 7))
    })
    private GeoAddress arrival;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "available_volume_cm3", precision = 8, scale = 2)
    private BigDecimal availableVolumeCm3;

    @Column(name = "available_weight_kg", precision = 8, scale = 2)
    private BigDecimal availableWeightKg;

    @Column(name = "price_per_kg", precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    @Column(name = "instant_booking")
    private boolean instantBooking;

    @Column(name = "max_detour_km", nullable = false, precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal maxDetourKm = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private TripStatus status = TripStatus.PUBLISHED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopOrder ASC")
    @Builder.Default
    private List<TripStop> stops = new ArrayList<>();


    public void addStop(TripStop stop) {
        stops.add(stop);
        stop.setTrip(this);
    }

    public void addStops(List<TripStop> stops) {
        for(var  stop : stops) {
            addStop(stop);
        }
    }
    public void updateStops(List<TripStop> newStops) {
        clearStops();
        addStops(newStops);
    }

    public void removeStop(TripStop stop) {
        stops.remove(stop);
        stop.setTrip(null);
    }

    public void clearStops(){
        stops.clear();
    }

    public void reorderStops() {
        for (int i = 0; i < stops.size(); i++) {
            stops.get(i).setStopOrder(i + 1);
        }
    }
}