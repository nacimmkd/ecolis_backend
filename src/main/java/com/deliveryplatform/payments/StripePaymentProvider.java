package com.deliveryplatform.payments;

import com.deliveryplatform.payments.dto.*;
import com.deliveryplatform.payments.exceptions.PaymentErrorCode;
import com.deliveryplatform.payments.exceptions.PaymentException;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCaptureParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StripePaymentProvider implements PaymentProvider {

    private final StripeClient stripe;
    private final long sessionValidityMinutes;

    public StripePaymentProvider(StripeClient stripe,
                                 @Value("${stripe.checkout.validity-minutes:60}") long sessionValidityMinutes) {
        this.stripe = stripe;
        this.sessionValidityMinutes = sessionValidityMinutes;
    }

    /**
     * Session Checkout embarquee dans l'app :
     *  - UiMode.EMBEDDED : le formulaire est monte dans une div, pas de redirection
     *  - RedirectOnCompletion.NEVER : l'utilisateur ne quitte jamais l'app
     *  - CaptureMethod.MANUAL : les fonds sont bloques, pas debites
     *  - CARD uniquement : les moyens de paiement differes ne supportent pas
     *    la capture manuelle
     */

    @Override
    public AuthorizeResponse authorize(AuthorizeRequest request) {
        SessionCreateParams params = SessionCreateParams.builder()
                .setUiMode(SessionCreateParams.UiMode.EMBEDDED)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setRedirectOnCompletion(SessionCreateParams.RedirectOnCompletion.NEVER)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(request.currency())
                                .setUnitAmount(request.amount())
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(request.label())
                                        .build())
                                .build())
                        .build())
                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                        .setCaptureMethod(SessionCreateParams.PaymentIntentData.CaptureMethod.MANUAL)
                        .setTransferGroup("request_" + request.requestId())
                        .putMetadata("requestId", request.requestId().toString())
                        .build())
                .setExpiresAt((System.currentTimeMillis() / 1000) + (sessionValidityMinutes * 60))
                .putMetadata("requestId", request.requestId().toString())
                .build();

        try {
            Session session = stripe.checkout().sessions().create(params);

            log.info("checkout session created requestId={} session={}",
                    request.requestId(), session.getId());

            return new AuthorizeResponse(session.getId(), session.getClientSecret());

        } catch (StripeException e) {
            throw fail("createCheckoutSession request=" + request.requestId(), e);
        }
    }

    @Override
    public CaptureResponse capture(String paymentIntentId, Long amountInCents) {
        PaymentIntentCaptureParams.Builder params = PaymentIntentCaptureParams.builder();
        if (amountInCents != null) {
            params.setAmountToCapture(amountInCents);
        }

        try {
            PaymentIntent intent = stripe.paymentIntents()
                    .capture(paymentIntentId, params.build(), key("capture-" + paymentIntentId));

            log.info("capture ok intent={} received={}", paymentIntentId, intent.getAmountReceived());
            return new CaptureResponse(intent.getId(), intent.getLatestCharge(), intent.getAmountReceived());

        } catch (StripeException e) {
            throw fail("capture intent=" + paymentIntentId, e);
        }
    }

    @Override
    public CancelResponse cancel(String paymentIntentId) {
        PaymentIntentCancelParams params = PaymentIntentCancelParams.builder()
                .setCancellationReason(PaymentIntentCancelParams.CancellationReason.ABANDONED)
                .build();

        try {
            PaymentIntent intent = stripe.paymentIntents()
                    .cancel(paymentIntentId, params, key("cancel-" + paymentIntentId));

            log.info("cancel ok intent={}", paymentIntentId);
            return new CancelResponse(intent.getId(), intent.getAmount());

        } catch (StripeException e) {
            throw fail("cancel intent=" + paymentIntentId, e);
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

    private RequestOptions key(String idempotencyKey) {
        return RequestOptions.builder().setIdempotencyKey(idempotencyKey).build();
    }

    private PaymentException fail(String context, StripeException e) {
        log.warn("stripe {} failed code={} : {}", context, e.getCode(), e.getMessage());
        return new PaymentException(PaymentErrorCode.STRIPE_REQUEST_FAILED,
                "Stripe " + context + " failed: " + e.getMessage(), e);
    }
}