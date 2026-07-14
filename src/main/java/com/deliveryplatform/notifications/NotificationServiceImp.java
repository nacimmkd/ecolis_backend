package com.deliveryplatform.notifications;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.notifications.channels.ChannelType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationManager notificationManager;


    @Override
    @Transactional
    public void notify(NotificationPayload payload) {
        notificationRepository.save(Notification.createFromNotificationPayload(payload));
        notificationManager.send(payload);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        var notification = getUserNotificationOrThrow(notificationId, userId);
        notification.setRead(true);
        notificationRepository.save(notification);
    }


    @Override
    @Transactional
    public void delete(UUID notificationId, UUID userId) {
        var notification = getUserNotificationOrThrow(notificationId, userId);
        notificationRepository.delete(notification);
    }


    // -------------------------------------------


    private Notification getUserNotificationOrThrow(UUID notificationId, UUID userId) {
        return notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification %s not found for user %s".formatted(notificationId, userId)
                ));
    }

}
