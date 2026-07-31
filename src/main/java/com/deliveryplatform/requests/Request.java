package com.deliveryplatform.requests;

import com.deliveryplatform.matching.Detour;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.payments.Payment;
import com.deliveryplatform.requests.exceptions.RequestErrorCode;
import com.deliveryplatform.requests.exceptions.RequestException;
import com.deliveryplatform.trips.Trip;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "booking_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@SQLRestriction("deleted = false")
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

    @OneToOne(mappedBy = "request")
    @Setter
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestState state = RequestState.PENDING;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "pickup_detour_km")
    private BigDecimal pickupDetourKm;

    @Column(name = "dropoff_detour_km")
    private BigDecimal dropOffDetourKm;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

    @Column(name = "requested_at")
    private OffsetDateTime requestedAt;

    @Column(name = "deleted")
    @Builder.Default
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    @Builder.Default
    private OffsetDateTime deletedAt = null;

    // ---- factory ------------------------------------------------------------

    public static Request create(Trip trip, Parcel parcel, Detour detour) {
        if (Objects.isNull(trip) || Objects.isNull(detour) || Objects.isNull(parcel))
            throw new RequestException(RequestErrorCode.MISSING_REQUIRED_DATA, "Required trip & parcel & detour to create a request");

        trip.assertMaxTripDetourRequirement(detour);
        trip.assertNotFull();
        parcel.assertIsAvailable();

        return Request.builder()
                .trip(trip)
                .parcel(parcel)
                .pickupDetourKm(BigDecimal.valueOf(detour.pickupDetourKm()))
                .dropOffDetourKm(BigDecimal.valueOf(detour.dropoffDetourKm()))
                .requestedAt(OffsetDateTime.now())
                .build();
    }

    // ---- invariants / guards ------------------------------------------------

    public void assertIsPending() {
        if (!isPending())
            throw new RequestException(
                    RequestErrorCode.INVALID_STATE_TRANSITION,
                    "Booking request is not pending, current state: %s".formatted(this.state)
            );
    }

    public void assertIsCarrier(UUID userId) {
        if (!userId.equals(this.getCarrier().getId()))
            throw new RequestException(RequestErrorCode.NOT_CARRIER, "User is not allowed to perform this action");
    }

    public void assertIsSender(UUID userId) {
        if (!userId.equals(this.getSender().getId()))
            throw new RequestException(RequestErrorCode.NOT_CARRIER, "User is not allowed to perform this action");
    }

    public void assertInvolves(UUID userId) {
        if (!involves(userId))
            throw new RequestException(RequestErrorCode.USER_NOT_INVOLVED, "User is not allowed to perform this action");
    }

    public void assertPaymentAuthorized(){
        if (payment == null || !payment.isAuthorized()) {
            throw new RequestException(RequestErrorCode.PAYMENT_REQUIRED,
                    "Request " + id + " must be paid before it can be sent to the carrier");
        }
    }

    // ---- lifecycle ------------------------------------------------------------

    public void accept() {
        this.state = RequestState.ACCEPTED;
        this.respondedAt = OffsetDateTime.now();
    }

    public void reject(String reason) {
        this.state = RequestState.REJECTED;
        this.rejectionReason = reason;
        this.respondedAt = OffsetDateTime.now();
        delete();
    }

    public void delete() {
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now();
    }

    // ---- queries ------------------------------------------------------------

    public boolean isPending() {
        return RequestState.PENDING.equals(this.state);
    }

    public boolean involves(UUID userId) {
        return getSender().getId().equals(userId) || getCarrier().getId().equals(userId);
    }

    public User getSender() {
        return this.parcel.getOwner();
    }

    public User getCarrier() {
        return this.trip.getOwner();
    }

    public long getPriceInCents(){
        var price = this.trip.getPricePerKg().multiply(this.parcel.getWeightKg());
        return price.movePointRight(2).longValueExact();
    }
}