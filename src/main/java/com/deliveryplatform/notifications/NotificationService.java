package com.deliveryplatform.notifications;

import com.deliveryplatform.notifications.dto.NotificationRequest;

import java.util.UUID;

public interface NotificationService {
    void notify(NotificationRequest request);
    void markAsRead(UUID notificationId, UUID userId);
    void delete(UUID notificationId, UUID userId);
}
