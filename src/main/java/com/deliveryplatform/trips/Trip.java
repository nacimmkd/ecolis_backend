package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // --- Departure address ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",     column = @Column(name = "departure_street",      nullable = false, columnDefinition = "TEXT")),
            @AttributeOverride(name = "city",       column = @Column(name = "departure_city",        nullable = false, length = 100)),
            @AttributeOverride(name = "postalCode", column = @Column(name = "departure_postal_code", nullable = false, length = 20)),
            @AttributeOverride(name = "country",    column = @Column(name = "departure_country",     nullable = false, length = 60)),
            @AttributeOverride(name = "lat",        column = @Column(name = "departure_lat",         precision = 10, scale = 7)),
            @AttributeOverride(name = "lng",        column = @Column(name = "departure_lng",         precision = 10, scale = 7)),
    })
    private Address departureAddress;

    // --- Arrival address ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",     column = @Column(name = "arrival_street",      nullable = false, columnDefinition = "TEXT")),
            @AttributeOverride(name = "city",       column = @Column(name = "arrival_city",        nullable = false, length = 100)),
            @AttributeOverride(name = "postalCode", column = @Column(name = "arrival_postal_code", nullable = false, length = 20)),
            @AttributeOverride(name = "country",    column = @Column(name = "arrival_country",     nullable = false, length = 60)),
            @AttributeOverride(name = "lat",        column = @Column(name = "arrival_lat",         precision = 10, scale = 7)),
            @AttributeOverride(name = "lng",        column = @Column(name = "arrival_lng",         precision = 10, scale = 7)),
    })
    private Address arrivalAddress;

    @Column(name = "estimated_arrival_at")
    private OffsetDateTime estimatedArrivalAt;

    @Column(name = "is_recurring", nullable = false)
    @Builder.Default
    private boolean recurring = false;

    @Column(name = "recurrence_days")
    private Integer recurrenceDays;

    @Column(name = "available_volume_m3")
    private BigDecimal availableVolumeM3;

    @Column(name = "max_parcels")
    private Integer maxParcels;

    @Column(name = "flat_price")
    private BigDecimal flatPrice;

    @Column(name = "max_detour_km")
    @Builder.Default
    private BigDecimal maxDetourKm = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TripStatus status = TripStatus.PUBLISHED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopOrder ASC")
    @Builder.Default
    private List<TripStop> stops = new ArrayList<>();
}
