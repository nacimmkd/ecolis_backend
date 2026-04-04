package com.deliveryplatform.notifications;


import lombok.Builder;

import java.util.Objects;
import java.util.UUID;

@Builder
public record NotificationRequest(
        UUID userId,
        String emailTo,
        NotificationType type,
        UUID referenceId
) {

    public NotificationRequest {
        Objects.requireNonNull(userId, "userId is required");
        Objects.requireNonNull(emailTo, "userEmail is required");
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(referenceId, "referenceId is required");
    }

    public Notification toEntity() {
        return Notification.builder()
                .userId(userId)
                .type(type)
                .referenceId(referenceId)
                .isRead(false)
                .build();
    }
}