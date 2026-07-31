package com.deliveryplatform.payments;

import com.deliveryplatform.payments.dto.PaymentResponse;
import com.deliveryplatform.payments.dto.WebhookRequest;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse checkout(UUID requestId);

    PaymentResponse cancel(UUID requestId);

    PaymentResponse capture(UUID requestId);

    boolean isAuthorized(UUID requestId);

    void handleWebHook(WebhookRequest webhookRequest);

}