package com.deliveryplatform.bookings;

import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.trips.Trip;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

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
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "pickup_code")
    private String pickupCode;

    @Column(name = "dropoff_code")
    private String dropOffCode;

    @Column(name = "confirmed_at")
    @Builder.Default
    private OffsetDateTime confirmedAt = OffsetDateTime.now();

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    // ----------------------------------------------------------------

    public static Booking createFromRequest(BookingRequest request) {
        return Booking.builder()
                .trip(request.getTrip())
                .parcel(request.getParcel())
                .price(request.getParcel().getWeightKg().multiply(request.getTrip().getPricePerKg()))
                .build();
    }

    public void pay() {
        this.status = BookingStatus.PAID;
        this.paidAt = OffsetDateTime.now();
    }

    public void complete() {
        this.status = BookingStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = OffsetDateTime.now();
    }

    public boolean isCompleted() {
        return BookingStatus.COMPLETED.equals(this.status);
    }

    public boolean involves(UUID userId) {
        return this.trip.getOwner().getId().equals(userId)
                || this.parcel.getOwner().getId().equals(userId);
    }

    public User resolveParticipant(UUID userId) {
        var parcelOwner = this.parcel.getOwner();
        var tripOwner   = this.trip.getOwner();
        return parcelOwner.getId().equals(userId) ? parcelOwner : tripOwner;
    }

    public User resolveOtherParticipant(UUID userId) {
        var parcelOwner = this.parcel.getOwner();
        var tripOwner   = this.trip.getOwner();
        return parcelOwner.getId().equals(userId) ? tripOwner : parcelOwner;
    }
}