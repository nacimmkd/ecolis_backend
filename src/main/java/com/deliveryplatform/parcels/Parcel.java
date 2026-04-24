package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String description;

    @Column(name = "weight_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelSize size;

    @Column(name = "is_fragile", nullable = false)
    private boolean fragile;

    @Column(name = "code_otp")
    private String codeOTP;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ParcelStatus status = ParcelStatus.PUBLISHED;

    @Column(name = "deadline_date")
    private LocalDate deadlineDate;


    @Embedded
    @AttributeOverride(name = "street",     column = @Column(name = "pickup_street",      nullable = false))
    @AttributeOverride(name = "city",       column = @Column(name = "pickup_city",        nullable = false))
    @AttributeOverride(name = "postalCode", column = @Column(name = "pickup_postal_code", nullable = false))
    @AttributeOverride(name = "country",    column = @Column(name = "pickup_country",     nullable = false))
    @AttributeOverride(name = "latitude",   column = @Column(name = "pickup_lat"))
    @AttributeOverride(name = "longitude",  column = @Column(name = "pickup_lng"))
    private GeocodedAddress pickupAddress;


    @Embedded
    @AttributeOverride(name = "street",     column = @Column(name = "dropoff_street",      nullable = false))
    @AttributeOverride(name = "city",       column = @Column(name = "dropoff_city",        nullable = false))
    @AttributeOverride(name = "postalCode", column = @Column(name = "dropoff_postal_code", nullable = false))
    @AttributeOverride(name = "country",    column = @Column(name = "dropoff_country",     nullable = false))
    @AttributeOverride(name = "latitude",   column = @Column(name = "dropoff_lat"))
    @AttributeOverride(name = "longitude",  column = @Column(name = "dropoff_lng"))
    private GeocodedAddress dropoffAddress;

    @CreationTimestamp
    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();


    public boolean isAvailable() {
        return this.status == ParcelStatus.PUBLISHED;
    }

    public boolean isOwner(UUID userId) {
        return this.user.getId().equals(userId);
    }

    public void updateStatus(ParcelStatus status) {
        this.status = status;
    }

}