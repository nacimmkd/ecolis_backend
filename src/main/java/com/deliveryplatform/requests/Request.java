package com.deliveryplatform.requests;

import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.matching.Detour;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.trips.Trip;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "booking_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id", nullable = false)
    private Parcel parcel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "pickup_detour_km")
    private BigDecimal pickupDetourKm;

    @Column(name = "dropoff_detour_km")
    private BigDecimal dropOffDetourKm;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private OffsetDateTime requestedAt;

    // ----------------------------------------------------------------

    public static Request create(Trip trip, Parcel parcel, Detour detour) {
        if (Objects.isNull(trip) || Objects.isNull(detour) || Objects.isNull(parcel))
            throw new InvalidDomainStateException("required trip & parcel & detour to create a request");

        assertMaxTripDetourRequirementOrThrow(trip, detour);

        return Request.builder()
                .trip(trip)
                .parcel(parcel)
                .pickupDetourKm(BigDecimal.valueOf(detour.pickupDetourKm()))
                .dropOffDetourKm(BigDecimal.valueOf(detour.pickupDetourKm()))
                .requestedAt(OffsetDateTime.now())
                .build();
    }

    private static void assertMaxTripDetourRequirementOrThrow(Trip trip, Detour detour) {
        if (!trip.isMaxDetourAccepted(BigDecimal.valueOf(detour.pickupDetourKm()), BigDecimal.valueOf(detour.dropoffDetourKm())))
            throw new InvalidDomainStateException("cannot create request : max detour not satisfied");
    }

    public void accept() {
        this.status = RequestStatus.ACCEPTED;
        this.respondedAt = OffsetDateTime.now();
    }

    public void reject(String reason) {
        this.status = RequestStatus.REJECTED;
        this.rejectionReason = reason;
        this.respondedAt = OffsetDateTime.now();
    }

    public void cancel() {
        this.status = RequestStatus.CANCELLED;
        this.respondedAt = OffsetDateTime.now();
    }

    public boolean isPending() {
        return RequestStatus.PENDING.equals(this.status);
    }

    public UUID getSenderId()  { return this.parcel.getOwner().getId(); }

    public UUID getCarrierId() { return this.trip.getOwner().getId();   }

    public boolean involves(UUID userId) {
        return getSenderId().equals(userId) || getCarrierId().equals(userId);
    }
}