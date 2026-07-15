package com.deliveryplatform.notifications;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record NotificationDto(
        UUID notificationId,
        NotificationType type,
        UUID referenceId,
        boolean isRead,
        Map<String, Object> payload,
        OffsetDateTime createdAt
) {
}
