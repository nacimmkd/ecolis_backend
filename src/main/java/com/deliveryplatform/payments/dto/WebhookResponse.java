package com.deliveryplatform.payments.dto;

import com.deliveryplatform.payments.PaymentStatus;

import java.util.UUID;

public record WebhookResponse(
        UUID bookingId,
        PaymentStatus status,
        String paymentIntentId
) {
}
