package com.deliveryplatform.notifications;


import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record NotificationRequest(
        UUID userId,
        NotificationType type,
        UUID referenceId,
        Object payload,
        String emailTo
) {
    public NotificationRequest {
        if (type.isSendEmail() && (emailTo == null || emailTo.isBlank())) {
            throw new IllegalArgumentException(
                    "emailTo is required for notification type " + type
            );
        }
        if (payload != null && !type.getPayloadType().isInstance(payload)) {
            throw new IllegalArgumentException(
                    "Invalid payload type %s for notification type %s"
                            .formatted(payload.getClass().getSimpleName(), type)
            );
        }
    }

    public Notification toEntity() {
        return Notification.builder()
                .userId(userId)
                .type(type)
                .referenceId(referenceId)
                .payload(payload)
                .build();
    }
}