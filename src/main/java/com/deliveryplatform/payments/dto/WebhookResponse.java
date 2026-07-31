package com.deliveryplatform.payments.dto;

import com.deliveryplatform.payments.PaymentStatus;

import java.util.UUID;

public record WebhookResponse(
        UUID requestId,
        PaymentStatus status,
        String paymentIntentId
) {
}
