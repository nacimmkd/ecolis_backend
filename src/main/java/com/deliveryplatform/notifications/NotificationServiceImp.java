package com.deliveryplatform.notifications;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.emails.EmailService;
import com.deliveryplatform.emails.EmailTemplates;
import com.deliveryplatform.notifications.dto.NotificationRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailNotifier;
    private final SimpUserRegistry simpUserRegistry;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String WS_DEST = "/queue/notifications";



    @Override
    @Transactional
    public void notify(NotificationRequest request) {
        var notification = notificationRepository.save(notificationMapper.toEntity(request));

        var isConnected = this.isUserConnected(notification.getUserId());
        if(isConnected){
            send(notification);
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

    private void send(Notification notification) {
        try{
            messagingTemplate.convertAndSendToUser(
                    notification.getUserId().toString(),
                    WS_DEST,
                    notification
            );
        }catch (Exception e){
            log.error("[WS] Failed to send notification — user={} — message={}", notification.getUserId().toString(), e.getMessage());
        }
    }

    private boolean isUserConnected(UUID userId) {
        return simpUserRegistry.getUser(userId.toString()) != null;
    }


}
