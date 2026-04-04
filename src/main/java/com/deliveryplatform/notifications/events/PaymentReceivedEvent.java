package com.deliveryplatform.notifications.events;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentReceivedEvent(
        UUID userId,
        String userEmail,
        BigDecimal amount,
        UUID bookingId
) {}
