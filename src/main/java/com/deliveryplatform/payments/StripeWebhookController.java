package com.deliveryplatform.payments;

import com.deliveryplatform.payments.exceptions.PaymentErrorCode;
import com.deliveryplatform.payments.exceptions.PaymentException;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks/stripe")
public class StripeWebhookController {

    private final PaymentService paymentService;
    private final String webhookSecret;

    public StripeWebhookController(PaymentService paymentService,
                                   @Value("${stripe.webhook-secret}") String webhookSecret) {
        this.paymentService = paymentService;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload,
                                              @RequestHeader("Stripe-Signature") String signature) {
        Event event = verifySignature(payload, signature);

        switch (event.getType()) {

            case "checkout.session.completed" -> {
                Session session = payload(event, Session.class);
                paymentService.handleCheckoutCompleted(session.getId(), session.getPaymentIntent());
            }

            case "checkout.session.expired" ->
                    paymentService.handleCheckoutExpired(payload(event, Session.class).getId());

            case "payment_intent.succeeded" -> {
                PaymentIntent intent = payload(event, PaymentIntent.class);
                paymentService.handleCaptured(intent.getId(), intent.getLatestCharge());
            }

            case "payment_intent.payment_failed" ->
                    paymentService.handleFailed(payload(event, PaymentIntent.class).getId());

            case "payment_intent.canceled" ->
                    paymentService.handleCanceled(payload(event, PaymentIntent.class).getId());

            default -> log.debug("unhandled stripe event type={}", event.getType());
        }

        return ResponseEntity.ok().build();
    }

    private Event verifySignature(String payload, String signature) {
        try {
            return Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new PaymentException(PaymentErrorCode.INVALID_WEBHOOK_SIGNATURE,
                    "Invalid Stripe webhook signature");
        }
    }


    private <T extends StripeObject> T payload(Event event, Class<T> type) {
        var deserializer = event.getDataObjectDeserializer();

        StripeObject object = deserializer.getObject().orElseGet(() -> {
            try {
                return deserializer.deserializeUnsafe();
            } catch (EventDataObjectDeserializationException e) {
                throw new PaymentException(PaymentErrorCode.STRIPE_REQUEST_FAILED,
                        "Could not deserialize event " + event.getId(), e);
            }
        });

        if (!type.isInstance(object)) {
            throw new PaymentException(PaymentErrorCode.STRIPE_REQUEST_FAILED,
                    "Unexpected payload for event " + event.getId() + " (" + event.getType() + ")");
        }
        return type.cast(object);
    }
}