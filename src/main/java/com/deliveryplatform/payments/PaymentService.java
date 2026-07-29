package com.deliveryplatform.payments;

import com.deliveryplatform.payments.dto.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse authorize(UUID requestId);

    PaymentResponse getPaymentForRequest(UUID requestId);

    PaymentResponse capture(UUID requestId);

    PaymentResponse cancel(UUID requestId);

    boolean isAuthorized(UUID requestId);

    // ---- webhooks ----------------------------------------

    void handleCheckoutCompleted(String checkoutSessionId, String paymentIntentId);

    void handleCheckoutExpired(String checkoutSessionId);

    void handleCaptured(String paymentIntentId, String chargeId);

    void handleFailed(String paymentIntentId);

    void handleCanceled(String paymentIntentId);
}