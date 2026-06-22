package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.images.Image;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

@Entity
@Table(name = "parcels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    private String description;

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

    @OneToOne
    @JoinColumn(name = "thumbnail_image_id")
    private Image thumbnail;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "parcel_images",
            joinColumns = @JoinColumn(name = "parcel_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<Image> images = new ArrayList<>();

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

    // ── Ownership ────────────────────────────────────────────────────────────

    public boolean isOwner(UUID userId) {
        return this.owner.getId().equals(userId);
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now();
        this.thumbnail = null;
        this.images.clear();
    }

    public void updateState(ParcelState state) {
        this.state = state;
        addTrackingEvent(TrackEvent.of(state, state.getMessage()));
    }

    // ── Images ───────────────────────────────────────────────────────────────

    public void addImage(Image image) {
        if (image.isConfirmed() && image.isOwnedBy(this.owner)) {
            this.images.add(image);
        }
    }

    public void addImages(List<Image> images) {
        images.forEach(this::addImage);
    }

    public void removeImages(List<Image> toRemove) {
        var idsToRemove = toRemove.stream().map(Image::getId).collect(toSet());
        this.images.removeIf(img -> idsToRemove.contains(img.getId()));
    }

    public void removeAllImages() {
        this.images.clear();
    }

    // ── Tracking ─────────────────────────────────────────────────────────────

    private void addTrackingEvent(TrackEvent event) {
        event.setParcel(this);
        this.trackEvents.add(event);
    }
}