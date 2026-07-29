package com.deliveryplatform.payments;

import com.deliveryplatform.requests.Request;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    @Column(name = "stripe_checkout_session_id", nullable = false, unique = true)
    private String stripeCheckoutSessionId;

    @Column(name = "stripe_payment_intent_id", unique = true)
    private String stripePaymentIntentId;

    @Column(name = "stripe_charge_id")
    private String stripeChargeId;

    @Column(nullable = false)
    private long amount;

    @Column(name = "application_fee_amount", nullable = false)
    private long applicationFeeAmount;

    @Column(nullable = false)
    private String currency;

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

    @Version
    @Column(nullable = false)
    @Builder.Default
    private long version = 0L;

    // ---- factory ------------------------------------------------------------

    public static Payment create(Request request, String stripeCheckoutSessionId,
                                 long amount, long applicationFeeAmount, String currency) {
        return Payment.builder()
                .request(request)
                .payer(request.getSender())
                .stripeCheckoutSessionId(stripeCheckoutSessionId)
                .amount(amount)
                .applicationFeeAmount(applicationFeeAmount)
                .currency(currency)
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

    public boolean hasPaymentIntent() {
        return this.stripePaymentIntentId != null;
    }

    public long getPayoutAmount() {
        return this.amount - this.applicationFeeAmount;
    }
}