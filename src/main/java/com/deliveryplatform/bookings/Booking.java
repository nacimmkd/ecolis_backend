package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.exceptions.BookingErrorCode;
import com.deliveryplatform.bookings.exceptions.BookingException;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.payments.Price;
import com.deliveryplatform.matching.Detour;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelState;
import com.deliveryplatform.payments.Payment;
import com.deliveryplatform.trips.Trip;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
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

    @OneToOne(mappedBy = "booking")
    @Setter
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingState state = BookingState.PENDING;

    @Embedded
    @AttributeOverride(name = "amountInCents", column = @Column(name = "price_amount_in_cents", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "price_currency", nullable = false, length = 3))
    private Price price;

    @Column(name = "pickup_detour_km", nullable = false)
    private BigDecimal pickupDetourKm;

    @Column(name = "dropoff_detour_km", nullable = false)
    private BigDecimal dropOffDetourKm;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "pickup_code")
    private String pickupCode;

    @Column(name = "dropoff_code")
    @Builder.Default
    private String dropOffCode = null;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

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

    public static Booking create(Trip trip, Parcel parcel, Detour detour, Price price) {
        if (Objects.isNull(trip) || Objects.isNull(parcel) || Objects.isNull(detour) || Objects.isNull(price))
            throw new BookingException(BookingErrorCode.MISSING_REQUIRED_DATA,
                    "Required trip & parcel & detour & price to create a booking");

        trip.assertMaxTripDetourRequirement(detour);
        trip.assertNotFull();
        parcel.assertIsAvailable();

        return Booking.builder()
                .trip(trip)
                .parcel(parcel)
                .price(price)
                .pickupDetourKm(BigDecimal.valueOf(detour.pickupDetourKm()))
                .dropOffDetourKm(BigDecimal.valueOf(detour.dropoffDetourKm()))
                .pickupCode(CodeGeneratorUtil.generateBookingCode())
                .build();
    }

    // ---- guards ------------------------------------------------

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

    public void assertIsCarrier(UUID userId) {
        if (!userId.equals(getCarrier().getId()))
            throw new BookingException(BookingErrorCode.NOT_CARRIER, "User is not allowed to perform this action");
    }

    public void assertIsSender(UUID userId) {
        if (!userId.equals(getSender().getId()))
            throw new BookingException(BookingErrorCode.NOT_CARRIER, "User is not allowed to perform this action");
    }

    public void assertPaymentAuthorized() {
        if (payment == null || !payment.isAuthorized())
            throw new BookingException(BookingErrorCode.PAYMENT_REQUIRED,
                    "Booking " + id + " must be paid before it can be sent to the carrier");
    }

    public void assertIsValidPickUpCode(String pickupCode) {
        if (!this.pickupCode.equals(pickupCode))
            throw new BookingException(BookingErrorCode.INVALID_PICKUP_CODE, "Pickup code is invalid");
    }

    public void assertIsValidDropOffCode(String dropOffCode) {
        if (!this.dropOffCode.equals(dropOffCode))
            throw new BookingException(BookingErrorCode.INVALID_DROPOFF_CODE, "DropOff code is invalid");
    }

    // ---- lifecycle ------------------------------------------------------------

    public void accept(UUID userId) {
        assertIsInState(BookingState.WAITING_FOR_ANSWER, "Booking is not waiting for answer");
        assertIsCarrier(userId);

        this.state = BookingState.ACCEPTED;
        this.respondedAt = OffsetDateTime.now();
        this.parcel.updateState(ParcelState.BOOKED);
        this.trip.reserveBooking(this);
    }

    public void reject(UUID userId, String reason) {
        assertIsInState(BookingState.WAITING_FOR_ANSWER, "Booking is not waiting for answer");
        assertIsCarrier(userId);

        this.state = BookingState.REJECTED;
        this.rejectionReason = reason;
        this.respondedAt = OffsetDateTime.now();
    }

    public void expire() {
        this.state = BookingState.REJECTED;
        this.rejectionReason = "Timed out";
    }

    public void sendRequest(){
        assertIsInState(BookingState.PENDING, "Can not request driver");
        assertPaymentAuthorized();
        this.state = BookingState.WAITING_FOR_ANSWER;
    }

    public void confirmPickUp(String pickupCode) {
        assertIsValidPickUpCode(pickupCode);

        this.parcel.updateState(ParcelState.IN_TRANSIT);
        this.pickupCode = null;
        this.dropOffCode = CodeGeneratorUtil.generateBookingCode();
    }

    public void complete(String dropOffCode) {
        assertIsValidDropOffCode(dropOffCode);
        this.state = BookingState.COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }

    public void cancel(UUID userId, String reason, CancelledBy cancelledBy) {

        assertUserInvolved(userId);

        this.parcel.updateState(ParcelState.PUBLISHED);
        this.trip.removeBooking(this);

        this.state = BookingState.CANCELLED;
        this.cancelledAt = OffsetDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancelReason = reason;
    }

    // ---- queries ------------------------------------------------------------


    public boolean involves(UUID userId) {
        return this.trip.getOwner().getId().equals(userId)
                || this.parcel.getOwner().getId().equals(userId);
    }

    public User getSender() {
        return this.parcel.getOwner();
    }

    public User getCarrier() {
        return this.trip.getOwner();
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
