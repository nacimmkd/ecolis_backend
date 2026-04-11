package com.deliveryplatform.notifications;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.notifications.dto.NotificationRequest;
import com.deliveryplatform.notifications.email.EmailNotificationService;
import com.deliveryplatform.notifications.email.EmailTemplates;
import com.deliveryplatform.notifications.inApp.InAppNotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final InAppNotificationService inAppNotifier;
    private final EmailNotificationService emailNotifier;
    private final SimpUserRegistry simpUserRegistry;
    private final NotificationMapper notificationMapper;


    @Override
    @Transactional
    public void notify(NotificationRequest request) {
        var notification = notificationRepository.save(notificationMapper.toEntity(request));

        var isConnected = this.isUserConnected(notification.getUserId());
        if(isConnected){
            inAppNotifier.send(notification);
        } else {
            var template = EmailTemplates.notificationReminderTemplate();
            emailNotifier.send(
                    request.emailTo(),
                    template.subject(),
                    template.body()
            );
        }
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

    private boolean isUserConnected(UUID userId) {
        return simpUserRegistry.getUser(userId.toString()) != null;
    }


}
