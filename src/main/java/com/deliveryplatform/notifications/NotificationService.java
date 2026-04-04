package com.deliveryplatform.notifications;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final InAppNotificationService inAppNotifier;
    private final EmailNotificationService emailNotifier;


    @Transactional
    public void notify(NotificationRequest request) {
        var notification = notificationRepository.save(request.toEntity());
        inAppNotifier.send(
                request.userId().toString(),
                notification
        );
        emailNotifier.send(
                request.emailTo(),
                EmailTemplates.NotificationReminderTemplate().subject(),
                EmailTemplates.NotificationReminderTemplate().body()
        );
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        var notification = getNotificationOrThrow(notificationId, userId);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void delete(UUID notificationId, UUID userId) {
        var notification = getNotificationOrThrow(notificationId, userId);
        notificationRepository.delete(notification);
    }


    // -------------------------------------------


    private Notification getNotificationOrThrow(UUID notificationId, UUID userId) {
        return notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification %s not found for user %s".formatted(notificationId, userId)
                ));
    }


}
