package com.deliveryplatform.notifications;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID notificationId,
        NotificationType type,
        UUID referenceId,
        boolean isRead,
        OffsetDateTime createdAt
) {
}
