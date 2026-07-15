package com.deliveryplatform.notifications;

import java.util.List;
import java.util.UUID;

interface NotificationService {
    void notify(NotificationEvent request);
    NotificationDto getNotificationsById(UUID notifId, UUID userId);
    List<NotificationDto> getUserNotifications(UUID userId);
    void markAsRead(UUID notificationId, UUID userId);
    void delete(UUID notificationId, UUID userId);
}
