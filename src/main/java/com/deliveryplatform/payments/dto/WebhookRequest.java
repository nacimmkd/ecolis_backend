package com.deliveryplatform.payments.dto;

public record WebhookRequest(
        String signature,
        String payload
) {}
