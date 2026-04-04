package com.deliveryplatform.notifications.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ParcelDeliveredEvent(
        UUID userId,
        String userEmail,
        UUID parcelId
) {}
