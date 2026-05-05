package com.deliveryplatform.notifications;

import java.util.UUID;

public interface NotificationService {
    void notify(NotificationPayload request);
    void markAsRead(UUID notificationId, UUID userId);
    void delete(UUID notificationId, UUID userId);
}
