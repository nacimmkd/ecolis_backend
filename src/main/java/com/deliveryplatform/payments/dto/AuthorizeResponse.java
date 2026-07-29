package com.deliveryplatform.payments.dto;


public record AuthorizeResponse(
        String checkoutSessionId,
        String clientSecret
) {}