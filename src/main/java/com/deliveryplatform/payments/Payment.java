package com.deliveryplatform.payments;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    @Column(name = "stripe_checkout_session_id", nullable = false, unique = true)
    private String stripeCheckoutSessionId;

    @Column(name = "stripe_payment_intent_id", unique = true)
    private String stripePaymentIntentId;

    @Column(name = "stripe_charge_id")
    private String stripeChargeId;

    @Column(name = "stripe_refund_id")
    private String stripeRefundId;

    @Embedded
    @AttributeOverride(name = "amountInCents", column = @Column(name = "amount", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false, length = 3))
    private Price price;

    @Column(name = "application_fee_amount", nullable = false)
    private long applicationFeeAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "authorized_at")
    private OffsetDateTime authorizedAt;

    @Column(name = "succeeded_at")
    private OffsetDateTime succeededAt;

    @Column(name = "canceled_at")
    private OffsetDateTime canceledAt;

    @Column(name = "refunded_at")
    private OffsetDateTime refundedAt;

    @Version
    @Column(nullable = false)
    @Builder.Default
    private long version = 0L;

    // ---- factory ------------------------------------------------------------

    public static Payment create(Booking booking, String stripeCheckoutSessionId,
                                 Price price, long applicationFeeAmount) {
        return Payment.builder()
                .booking(booking)
                .payer(booking.getSender())
                .stripeCheckoutSessionId(stripeCheckoutSessionId)
                .price(price)
                .applicationFeeAmount(applicationFeeAmount)
                .build();
    }

    // ---- lifecycle ------------------------------------------------------------

    public void markAuthorized(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.status = PaymentStatus.AUTHORIZED;
        this.authorizedAt = OffsetDateTime.now();
    }

    public void markSucceeded(String stripeChargeId) {
        this.status = PaymentStatus.SUCCEEDED;
        this.stripeChargeId = stripeChargeId;
        this.succeededAt = OffsetDateTime.now();
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void markCanceled() {
        this.status = PaymentStatus.CANCELED;
        this.canceledAt = OffsetDateTime.now();
    }

    public void markRefunded(String stripeRefundId) {
        this.status = PaymentStatus.REFUNDED;
        this.stripeRefundId = stripeRefundId;
        this.refundedAt = OffsetDateTime.now();
    }

    // ---- queries ------------------------------------------------------------

    public boolean isPending() {
        return PaymentStatus.PENDING.equals(this.status);
    }

    public boolean isAuthorized() {
        return PaymentStatus.AUTHORIZED.equals(this.status);
    }

    public boolean isCaptured() {
        return PaymentStatus.SUCCEEDED.equals(this.status);
    }

    public boolean isRefunded() {
        return PaymentStatus.REFUNDED.equals(this.status);
    }

    public boolean hasPaymentIntent() {
        return this.stripePaymentIntentId != null;
    }

    public long getPayoutAmount() {
        return this.price.getAmountInCents() - this.applicationFeeAmount;
    }
}