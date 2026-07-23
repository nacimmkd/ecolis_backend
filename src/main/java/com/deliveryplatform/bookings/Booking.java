package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.exceptions.BookingErrorCode;
import com.deliveryplatform.bookings.exceptions.BookingException;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelState;
import com.deliveryplatform.requests.Request;
import com.deliveryplatform.requests.RequestState;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
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
    private BookingState state = BookingState.PENDING;

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

    // ---- factory ------------------------------------------------------------

    public static Booking createFromRequest(Request request) {
        var parcel = request.getParcel();
        var trip = request.getTrip();

        assertValidRequestStatusOrThrow(request);

        var booking = Booking.builder()
                .parcel(parcel)
                .trip(trip)
                .price(BookingPriceCalculator.calculate(parcel, trip))
                .pickupCode(CodeGeneratorUtil.generateBookingCode())
                .build();

        parcel.updateState(ParcelState.BOOKED);
        trip.reserveBooking(booking);
        return booking;
    }

    // ---- invariants / guards ------------------------------------------------

    public void assertUserInvolved(UUID userId) {
        if (!involves(userId))
            throw new BookingException(BookingErrorCode.NOT_INVOLVED_IN_BOOKING, "User is not part of this booking");
    }

    public void assertIsCompleted() {
        if (!BookingState.COMPLETED.equals(this.state))
            throw new BookingException(BookingErrorCode.INVALID_STATE, "Booking must be completed before reviewing");
    }

    public void assertIsInState(BookingState expected, String errorMessage) {
        if (!expected.equals(this.state))
            throw new BookingException(BookingErrorCode.INVALID_STATE, errorMessage);
    }

    private static void assertValidRequestStatusOrThrow(Request request) {
        if (!RequestState.ACCEPTED.equals(request.getState()))
            throw new BookingException(BookingErrorCode.REQUEST_NOT_ACCEPTED, "Cannot create booking: request is not accepted");
    }

    // ---- lifecycle ------------------------------------------------------------

    public void pay() {
        this.state = BookingState.PAID;
        this.paidAt = OffsetDateTime.now();
    }

    public void confirmPickUp(String pickupCode) {
        if (!this.pickupCode.equals(pickupCode))
            throw new BookingException(BookingErrorCode.INVALID_PICKUP_CODE, "Pickup code is invalid");
        this.parcel.updateState(ParcelState.IN_TRANSIT);
        this.pickupCode = null;
        this.dropOffCode = CodeGeneratorUtil.generateBookingCode();
    }

    public void complete(String dropOffCode) {
        confirmDropOff(dropOffCode);
        this.state = BookingState.COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }

    public void cancel(String reason, CancelledBy cancelledBy) {
        this.state = BookingState.CANCELLED;
        this.parcel.updateState(ParcelState.PUBLISHED);
        this.trip.removeBooking(this);
        this.cancelledAt = OffsetDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancelReason = reason;
    }

    private void confirmDropOff(String dropOffCode) {
        if (!this.dropOffCode.equals(dropOffCode))
            throw new BookingException(BookingErrorCode.INVALID_DROPOFF_CODE, "Dropoff code is invalid");
        this.parcel.updateState(ParcelState.DELIVERED);
        this.dropOffCode = null;
    }

    // ---- queries ------------------------------------------------------------

    public boolean involves(UUID userId) {
        return this.trip.getOwner().getId().equals(userId)
                || this.parcel.getOwner().getId().equals(userId);
    }

    public User getSender() {
        return this.parcel.getOwner();
    }

    public User resolveParticipant(UUID userId) {
        var parcelOwner = this.parcel.getOwner();
        var tripOwner = this.trip.getOwner();
        return parcelOwner.getId().equals(userId) ? parcelOwner : tripOwner;
    }

    public User resolveOtherParticipant(UUID userId) {
        var parcelOwner = this.parcel.getOwner();
        var tripOwner = this.trip.getOwner();
        return parcelOwner.getId().equals(userId) ? tripOwner : parcelOwner;
    }

    public CancelledBy resolveCanceller(UUID currentUserId) {
        if (currentUserId.equals(this.parcel.getOwner().getId())) return CancelledBy.SENDER;
        if (currentUserId.equals(this.trip.getOwner().getId())) return CancelledBy.CARRIER;
        return CancelledBy.ADMIN;
    }

    public BigDecimal getBookingWeight() {
        return this.parcel.getWeightKg();
    }
}