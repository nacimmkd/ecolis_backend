package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.parcels.dto.ParcelCreateRequest;
import com.deliveryplatform.parcels.exceptions.ParcelErrorCode;
import com.deliveryplatform.parcels.exceptions.ParcelException;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "parcels")
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@SQLRestriction("deleted = false")
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    private String title;

    @Column(name = "weight_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    private ParcelSize size;

    @Column(name = "is_fragile", nullable = false)
    private boolean fragile;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private ParcelState state = ParcelState.PUBLISHED;

    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "pickup_street", nullable = false))
    @AttributeOverride(name = "city", column = @Column(name = "pickup_city", nullable = false))
    @AttributeOverride(name = "postalCode", column = @Column(name = "pickup_postal_code", nullable = false))
    @AttributeOverride(name = "country", column = @Column(name = "pickup_country", nullable = false))
    @AttributeOverride(name = "latitude", column = @Column(name = "pickup_lat"))
    @AttributeOverride(name = "longitude", column = @Column(name = "pickup_lng"))
    private Address pickupAddress;


    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "dropoff_street", nullable = false))
    @AttributeOverride(name = "city", column = @Column(name = "dropoff_city", nullable = false))
    @AttributeOverride(name = "postalCode", column = @Column(name = "dropoff_postal_code", nullable = false))
    @AttributeOverride(name = "country", column = @Column(name = "dropoff_country", nullable = false))
    @AttributeOverride(name = "latitude", column = @Column(name = "dropoff_lat"))
    @AttributeOverride(name = "longitude", column = @Column(name = "dropoff_lng"))
    private Address dropoffAddress;

    @OneToMany(mappedBy = "parcel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ParcelImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "parcel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrackEvent> trackEvents = new ArrayList<>();

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;


    public static Parcel createFromRequest(
            ParcelCreateRequest request, User owner, Address pickupAddress,
            Address dropoffAddress
    ) {
        Parcel parcel = Parcel.builder()
                .owner(owner)
                .title(request.title())
                .weightKg(request.weightKg())
                .size(request.size())
                .fragile(request.fragile())
                .pickupAddress(pickupAddress)
                .dropoffAddress(dropoffAddress)
                .build();

        parcel.addTrackingEvent(TrackEvent.of(parcel.getState(), parcel.getState().getMessage()));

        return parcel;
    }

    // ── Ownership ────────────────────────────────────────────────────────────

    public UUID getOwnerId() {
        return this.owner.getId();
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now();
        this.images.clear();
    }

    public void updateState(ParcelState state) {
        assertNotDeleted();
        this.state = state;
        addTrackingEvent(TrackEvent.of(state, state.getMessage()));
    }

    // ── Images ───────────────────────────────────────────────────────────────

    public void addImage(ParcelImage image) {
        image.setParcel(this);
        this.images.add(image);
    }

    public void removeImage(ParcelImage image) {
        this.images.remove(image);
    }

    public void removeAllImages() {
        this.images.clear();
    }

    // ── Tracking ─────────────────────────────────────────────────────────────

    private void addTrackingEvent(TrackEvent event) {
        event.setParcel(this);
        this.trackEvents.add(event);
    }

    // ── Assertions ─────────────────────────────────────────────────────────────

    public void assertOwnedBy(UUID userId) {
        if (!userId.equals(this.owner.getId()))
            throw new ParcelException(ParcelErrorCode.PARCEL_NOT_OWNED, "User with id: %s is not owner of this parcel".formatted(userId));
    }

    public void assertNotDeleted() {
        if (this.deleted)
            throw new ParcelException(ParcelErrorCode.PARCEL_DELETED, "Action can not be performed: parcel is deleted");
    }

    public void assertIsInState(List<ParcelState> states) {
        if (!states.contains(this.state))
            throw new ParcelException(ParcelErrorCode.PARCEL_INVALID_STATE, "Parcel is not in a valid state for this operation");
    }

    public void assertIsAvailable() {
        if (!ParcelState.PUBLISHED.equals(this.state))
            throw new ParcelException(ParcelErrorCode.PARCEL_NOT_AVAILABLE, "Parcel is not available");
    }
}