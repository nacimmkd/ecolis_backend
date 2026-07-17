package com.deliveryplatform.notifications;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationManager notificationManager;
    private final NotificationMapper notificationMapper;


    @Override
    @Transactional
    public void notify(NotificationEvent event) {
        if (event.getNotificationType().isPersistent()) {
            notificationRepository.save(Notification.createFromNotificationEvent(event));
        }
        notificationManager.send(event);
    }

    @Override
    public List<NotificationDto> getUserNotifications(UUID userId){
        return notificationMapper.toDto(
                notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
        );
    }

    @Override
    public NotificationDto getNotificationsById(UUID notifId, UUID userId){
        var notif = getUserNotificationOrThrow(notifId, userId);
        return notificationMapper.toDto(notif);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        var notification = getUserNotificationOrThrow(notificationId, userId);
        notification.read();
        notificationRepository.save(notification);
    }


    @Override
    @Transactional
    public void delete(UUID notificationId, UUID userId) {
        var notification = getUserNotificationOrThrow(notificationId, userId);
        notification.delete();
        notificationRepository.save(notification);
    }


    // -------------------------------------------


    private Notification getUserNotificationOrThrow(UUID notificationId, UUID userId) {
        return notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification %s not found for user %s".formatted(notificationId, userId)
                ));
    }

}
