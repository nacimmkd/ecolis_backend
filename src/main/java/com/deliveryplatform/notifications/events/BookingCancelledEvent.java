package com.deliveryplatform.notifications.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BookingCancelledEvent(
        UUID userId,
        String userEmail,
        UUID bookingId
) {}
