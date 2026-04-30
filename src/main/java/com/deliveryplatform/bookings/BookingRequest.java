package com.deliveryplatform.bookings;

import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.trips.Trip;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

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
    private BookingRequestStatus status = BookingRequestStatus.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime requestedAt;

    // ----------------------------------------------------------------

    public static BookingRequest create(Trip trip, Parcel parcel) {
        return BookingRequest.builder()
                .trip(trip)
                .parcel(parcel)
                .price(parcel.getWeightKg().multiply(trip.getPricePerKg()))
                .build();
    }

    public void accept() {
        this.status = BookingRequestStatus.ACCEPTED;
        this.respondedAt = OffsetDateTime.now();
    }

    public void reject(String reason) {
        this.status = BookingRequestStatus.REJECTED;
        this.rejectionReason = reason;
        this.respondedAt = OffsetDateTime.now();
    }

    public void cancel() {
        this.status = BookingRequestStatus.CANCELLED;
        this.respondedAt = OffsetDateTime.now();
    }

    public boolean isPending() {
        return BookingRequestStatus.PENDING.equals(this.status);
    }

    public boolean isTerminal() {
        return status == BookingRequestStatus.ACCEPTED
                || status == BookingRequestStatus.REJECTED
                || status == BookingRequestStatus.CANCELLED;
    }

    public UUID getSenderId()  { return this.parcel.getOwner().getId(); }
    public UUID getCarrierId() { return this.trip.getOwner().getId();   }

    public boolean involves(UUID userId) {
        return getSenderId().equals(userId) || getCarrierId().equals(userId);
    }
}