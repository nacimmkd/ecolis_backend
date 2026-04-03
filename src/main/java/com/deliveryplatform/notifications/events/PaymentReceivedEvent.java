package com.deliveryplatform.notifications.events;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentReceivedEvent(
        UUID userId,
        String userEmail,
        BigDecimal amount,
        UUID bookingId
) {}
