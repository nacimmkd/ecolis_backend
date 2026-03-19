package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.Address;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "parcels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private String description;

    @Column(name = "weight_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "length_cm", precision = 6, scale = 1)
    private BigDecimal lengthCm;

    @Column(name = "width_cm", precision = 6, scale = 1)
    private BigDecimal widthCm;

    @Column(name = "height_cm", precision = 6, scale = 1)
    private BigDecimal heightCm;

    @Column(name = "is_fragile", nullable = false)
    private boolean isFragile;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ParcelStatus status = ParcelStatus.AVAILABLE;

    @Column(name = "deadline_date")
    private LocalDate deadlineDate;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",     column = @Column(name = "pickup_street",      nullable = false)),
            @AttributeOverride(name = "city",       column = @Column(name = "pickup_city",        nullable = false)),
            @AttributeOverride(name = "postalCode", column = @Column(name = "pickup_postal_code", nullable = false)),
            @AttributeOverride(name = "country",    column = @Column(name = "pickup_country",     nullable = false)),
            @AttributeOverride(name = "lat",        column = @Column(name = "pickup_lat")),
            @AttributeOverride(name = "lng",        column = @Column(name = "pickup_lng"))
    })
    private Address pickupAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",     column = @Column(name = "dropoff_street",      nullable = false)),
            @AttributeOverride(name = "city",       column = @Column(name = "dropoff_city",        nullable = false)),
            @AttributeOverride(name = "postalCode", column = @Column(name = "dropoff_postal_code", nullable = false)),
            @AttributeOverride(name = "country",    column = @Column(name = "dropoff_country",     nullable = false)),
            @AttributeOverride(name = "lat",        column = @Column(name = "dropoff_lat")),
            @AttributeOverride(name = "lng",        column = @Column(name = "dropoff_lng"))
    })
    private Address dropoffAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}