package com.deliveryplatform.payments.dto;

public record CaptureResponse(
        String paymentIntentId,
        String chargeId,
        long amountCaptured
) {}