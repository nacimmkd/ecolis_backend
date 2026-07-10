package com.deliveryplatform.bookings;

import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelState;
import com.deliveryplatform.requests.Request;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    @Setter
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id", nullable = false)
    @Setter
    private Parcel parcel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "pickup_code")
    private String pickupCode;

    @Column(name = "dropoff_code")
    @Builder.Default
    private String dropOffCode = null;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "paid_at")
    @Builder.Default
    private OffsetDateTime paidAt = null;

    @Column(name = "completed_at")
    @Builder.Default
    private OffsetDateTime completedAt = null;

    @Column(name = "cancelled_at")
    @Builder.Default
    private OffsetDateTime cancelledAt = null;

    @Column(name = "cancelled_by")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CancelledBy cancelledBy = null;

    @Column(name = "cancel_reason")
    @Builder.Default
    private String cancelReason = null;

    // ----------------------------------------------------------------

    public static Booking createFromRequest(Request request) {
        var parcel = request.getParcel();
        var trip = request.getTrip();
        var booking = Booking.builder()
                .parcel(request.getParcel())
                .trip(request.getTrip())
                .price(BookingPriceCalculator.calculate(request.getParcel(), request.getTrip()))
                .pickupCode(CodeGeneratorUtil.generateBookingCode())
                .build();
        parcel.updateState(ParcelState.BOOKED);
        trip.addBooking(booking);
        return booking;
    }

    public void pay() {
        this.status = BookingStatus.PAID;
        this.paidAt = OffsetDateTime.now();
    }

    public void complete(String dropOffCode) {
        confirmDropOff(dropOffCode);
        this.status = BookingStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }

    public void cancel(String reason, CancelledBy cancelledBy) {
        this.status = BookingStatus.CANCELLED;
        this.parcel.updateState(ParcelState.PUBLISHED);
        this.trip.removeBooking(this);
        this.cancelledAt = OffsetDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancelReason = reason;
    }

    public void confirmPickUp(String pickupCode) {
        if (!this.pickupCode.equals(pickupCode))
            throw new InvalidDomainStateException("pickup code is incorrect");
        this.parcel.updateState(ParcelState.IN_TRANSIT);
        this.pickupCode = null;

        // set dropOff code
        this.dropOffCode = CodeGeneratorUtil.generateBookingCode();
    }

    private void confirmDropOff(String dropOffCode) {
        if (!this.dropOffCode.equals(dropOffCode))
            throw new InvalidDomainStateException("dropOff code is incorrect");
        this.parcel.updateState(ParcelState.DELIVERED);
        this.dropOffCode = null;
    }

    public boolean isCompleted() {
        return BookingStatus.COMPLETED.equals(this.status);
    }


    public boolean involves(UUID userId) {
        return !this.trip.getOwner().getId().equals(userId)
                && !this.parcel.getOwner().getId().equals(userId);
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

    public CancelledBy resolveCanceller(UUID currentUserId) {
        if (currentUserId.equals(this.getParcel().getOwner().getId())) return CancelledBy.SENDER;
        if (currentUserId.equals(this.getTrip().getOwner().getId())) return CancelledBy.CARRIER;
        return CancelledBy.ADMIN;
    }

    public BigDecimal getBookingWeight() {
        return this.parcel.getWeightKg();
    }


}