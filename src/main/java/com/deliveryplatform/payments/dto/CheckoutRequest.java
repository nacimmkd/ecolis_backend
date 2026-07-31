package com.deliveryplatform.payments.dto;

import java.util.UUID;

public record CheckoutRequest(
        UUID requestId,
        long amount,
        String currency,
        String label
) {}