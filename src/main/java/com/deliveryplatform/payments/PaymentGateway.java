package com.deliveryplatform.payments;

import com.deliveryplatform.payments.dto.*;

import java.util.Optional;


public interface PaymentGateway {

    CheckoutSession checkout(CheckoutRequest request);

    void expireCheckoutSession(String checkoutSessionId);

    String retrieveClientSecret(String checkoutSessionId);

    void cancel(String paymentIntentId);

    String capture(String paymentIntentId, Long amountToCaptureInCents);

    String refund(String paymentIntentId, Long amountToRefundInCents);

    Optional<WebhookResponse> parseWebhookRequest(WebhookRequest request);
}