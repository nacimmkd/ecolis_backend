package com.deliveryplatform.notifications.events;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingAcceptedEvent(
        UUID senderId,
        String senderEmail,
        String carrierName,
        BigDecimal price,
        UUID bookingId
) {}
