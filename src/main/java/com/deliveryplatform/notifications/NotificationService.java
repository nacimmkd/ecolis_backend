package com.deliveryplatform.notifications;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void notify(NotificationPayload request);
    List<NotificationDto> getUserNotifications(UUID userId);
    void markAsRead(UUID notificationId, UUID userId);
    void delete(UUID notificationId, UUID userId);
}
