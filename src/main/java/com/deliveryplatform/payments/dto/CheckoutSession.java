package com.deliveryplatform.payments.dto;


public record CheckoutSession(
        String checkoutSessionId,
        String clientSecret
) {}