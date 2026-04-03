package com.deliveryplatform.notifications.events;

import java.util.UUID;

public record ParcelDeliveredEvent(
        UUID userId,
        String userEmail,
        UUID parcelId
) {}
