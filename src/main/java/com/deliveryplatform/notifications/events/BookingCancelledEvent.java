package com.deliveryplatform.notifications.events;

import java.util.UUID;

public record BookingCancelledEvent(
        UUID userId,
        String userEmail,
        UUID bookingId
) {}
