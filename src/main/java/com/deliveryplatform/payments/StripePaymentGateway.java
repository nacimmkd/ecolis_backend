package com.deliveryplatform.payments;

import com.deliveryplatform.payments.dto.*;
import com.deliveryplatform.payments.exceptions.PaymentErrorCode;
import com.deliveryplatform.payments.exceptions.PaymentException;
import com.stripe.StripeClient;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCaptureParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway {

    private final StripeClient stripe;
    private final static long sessionValidityMinutes = 60;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;


    @Override
    public CheckoutSession checkout(CheckoutRequest request) {
        SessionCreateParams params = SessionCreateParams.builder()
                .setUiMode(SessionCreateParams.UiMode.EMBEDDED)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setRedirectOnCompletion(SessionCreateParams.RedirectOnCompletion.NEVER)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(request.amount().getCurrency())
                                .setUnitAmount(request.amount().getAmountInCents())
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(request.label())
                                        .build())
                                .build())
                        .build())
                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                        .setCaptureMethod(SessionCreateParams.PaymentIntentData.CaptureMethod.MANUAL)
                        .setTransferGroup("booking_" + request.bookingId())
                        .putMetadata("bookingId", request.bookingId().toString())
                        .build())
                .setExpiresAt((System.currentTimeMillis() / 1000) + (sessionValidityMinutes * 60))
                .putMetadata("bookingId", request.bookingId().toString())
                .build();

        try {
            Session session = stripe.checkout().sessions().create(params);

            log.info("checkout session created bookingId={} session={}",
                    request.bookingId(), session.getId());

            return new CheckoutSession(session.getId(), session.getClientSecret());

        } catch (StripeException e) {
            throw fail("checkout booking=" + request.bookingId(), e);
        }
    }

    @Override
    public void expireCheckoutSession(String checkoutSessionId) {
        try {
            stripe.checkout().sessions().expire(checkoutSessionId);
            log.info("checkout session expired session={}", checkoutSessionId);

        } catch (StripeException e) {
            throw fail("expireSession session=" + checkoutSessionId, e);
        }
    }

    @Override
    public String retrieveClientSecret(String checkoutSessionId) {
        try {
            Session session = stripe.checkout().sessions().retrieve(checkoutSessionId);

            if (!"open".equals(session.getStatus())) {
                log.info("checkout session no longer open session={} status={}",
                        checkoutSessionId, session.getStatus());
                return null;
            }
            return session.getClientSecret();

        } catch (StripeException e) {
            throw fail("retrieveSession session=" + checkoutSessionId, e);
        }
    }

    @Override
    public void cancel(String paymentIntentId) {
        PaymentIntentCancelParams params = PaymentIntentCancelParams.builder()
                .setCancellationReason(PaymentIntentCancelParams.CancellationReason.REQUESTED_BY_CUSTOMER)
                .build();

        try {
            PaymentIntent paymentIntent = stripe.paymentIntents().cancel(paymentIntentId, params,
                    key("cancel-" + paymentIntentId));

            log.info("payment intent canceled intent={} status={}", paymentIntentId, paymentIntent.getStatus());

        } catch (StripeException e) {
            throw fail("cancel intent=" + paymentIntentId, e);
        }
    }

    @Override
    public String capture(String paymentIntentId, Long amountToCaptureInCents) {
        PaymentIntentCaptureParams.Builder params = PaymentIntentCaptureParams.builder();
        if (amountToCaptureInCents != null) {
            params.setAmountToCapture(amountToCaptureInCents);
        }

        try {
            PaymentIntent paymentIntent = stripe.paymentIntents().capture(paymentIntentId, params.build(),
                    key("capture-" + paymentIntentId));

            log.info("payment intent captured intent={} status={} charge={}",
                    paymentIntentId, paymentIntent.getStatus(), paymentIntent.getLatestCharge());

            return paymentIntent.getLatestCharge();

        } catch (StripeException e) {
            throw fail("capture intent=" + paymentIntentId, e);
        }
    }


    @Override
    public Optional<WebhookResponse> parseWebhookRequest(WebhookRequest request) {
        Event event = verifySignature(request);
        return switch (event.getType()) {

            case "checkout.session.completed" -> {
                Session session = payload(event, Session.class);
                yield Optional.of(new WebhookResponse(
                        extractBookingId(session.getMetadata()),
                        PaymentStatus.AUTHORIZED,
                        session.getPaymentIntent()));
            }

            case "checkout.session.expired" -> {
                Session session = payload(event, Session.class);
                yield Optional.of(new WebhookResponse(
                        extractBookingId(session.getMetadata()),
                        PaymentStatus.CANCELED,
                        session.getPaymentIntent()));
            }

            case "payment_intent.succeeded" -> {
                PaymentIntent paymentIntent = payload(event, PaymentIntent.class);
                yield Optional.of(new WebhookResponse(
                        extractBookingId(paymentIntent.getMetadata()),
                        PaymentStatus.SUCCEEDED,
                        paymentIntent.getId()));
            }

            case "payment_intent.payment_failed" -> {
                PaymentIntent paymentIntent = payload(event, PaymentIntent.class);
                yield Optional.of(new WebhookResponse(
                        extractBookingId(paymentIntent.getMetadata()),
                        PaymentStatus.FAILED,
                        paymentIntent.getId()));
            }

            case "payment_intent.canceled" -> {
                PaymentIntent paymentIntent = payload(event, PaymentIntent.class);
                yield Optional.of(new WebhookResponse(
                        extractBookingId(paymentIntent.getMetadata()),
                        PaymentStatus.CANCELED,
                        paymentIntent.getId()));
            }

            default -> {
                log.debug("unhandled stripe event type={}", event.getType());
                yield Optional.empty();
            }
        };
    }

    @Override
    public String refund(String paymentIntentId, Long amountToRefundInCents) {
        RefundCreateParams.Builder params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId);
        if (amountToRefundInCents != null) {
            params.setAmount(amountToRefundInCents);
        }

        try {
            Refund refund = stripe.refunds().create(params.build(), key("refund-" + paymentIntentId));

            log.info("payment intent refunded intent={} status={} refund={}",
                    paymentIntentId, refund.getStatus(), refund.getId());

            return refund.getId();

        } catch (StripeException e) {
            throw fail("refund intent=" + paymentIntentId, e);
        }
    }

    // ---- infrastructure ---------------------------------------------------


    private Event verifySignature(WebhookRequest request) {

        var payload = request.payload();
        var signature = request.headers().get("stripe-signature");
        try {
            return Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new PaymentException(PaymentErrorCode.INVALID_WEBHOOK_SIGNATURE,
                    "Stripe invalid signature");
        }
    }

    private UUID extractBookingId(Map<String, String> metadata) {
        return UUID.fromString(metadata.get("bookingId"));
    }

    private <T extends StripeObject> T payload(Event event, Class<T> type) {
        try {
            return type.cast(event.getDataObjectDeserializer().deserializeUnsafe());
        } catch (EventDataObjectDeserializationException e) {
            throw new PaymentException(PaymentErrorCode.STRIPE_REQUEST_FAILED,
                    "Could not deserialize event " + event.getId(), e);
        }
    }

    private RequestOptions key(String idempotencyKey) {
        return RequestOptions.builder().setIdempotencyKey(idempotencyKey).build();
    }

    private PaymentException fail(String context, StripeException e) {
        log.warn("stripe {} failed code={} : {}", context, e.getCode(), e.getMessage());
        return new PaymentException(PaymentErrorCode.STRIPE_REQUEST_FAILED,
                "Stripe " + context + " failed: " + e.getMessage(), e);
    }
}