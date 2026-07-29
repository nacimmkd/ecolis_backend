package com.deliveryplatform.payments;

import com.deliveryplatform.payments.dto.*;


public interface PaymentProvider {

    AuthorizeResponse authorize(AuthorizeRequest request);

    CaptureResponse capture(String paymentIntentId, Long amountInCents);

    CancelResponse cancel(String paymentIntentId);

    void expireCheckoutSession(String checkoutSessionId);

    String retrieveClientSecret(String checkoutSessionId);
}