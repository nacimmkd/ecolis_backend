package com.deliveryplatform.payments.dto;

import java.util.UUID;

public record AuthorizeRequest(
        UUID requestId,
        long amount,
        String currency,
        String label
) {}