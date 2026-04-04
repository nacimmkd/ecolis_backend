package com.deliveryplatform.notifications.events;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BookingAcceptedEvent(
        UUID senderId,
        String senderEmail,
        String carrierName,
        BigDecimal price,
        UUID bookingId
) {}
