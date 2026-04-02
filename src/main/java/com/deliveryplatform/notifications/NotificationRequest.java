package com.deliveryplatform.notifications;


import java.util.Map;
import java.util.UUID;

public record NotificationRequest(
        UUID userId,
        NotificationType type,
        UUID referenceId,
        Map<String,Object> payload,
        String emailTo
        ) {


    public NotificationRequest {
        if (type.isSendEmail() && (emailTo == null || emailTo.isBlank())) {
            throw new IllegalArgumentException(
                    "[NotificationRequest] emailTo required for type " + type
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
