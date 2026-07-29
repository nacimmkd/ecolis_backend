package com.deliveryplatform.payments.dto;

public record CancelResponse(
        String paymentIntentId,
        long amountReleased
) {}