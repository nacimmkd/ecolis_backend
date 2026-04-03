package com.deliveryplatform.notifications;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private static final String WS_DEST = "/queue/notifications";

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;


    @Transactional
    public void notify(NotificationRequest request, UserPrincipal user) {
        var notification = request.toEntity();
        notificationRepository.save(notification);

        sendWebSocket(notification, user);

        if (request.type().isSendEmail()) {
            emailService.send(
                    request.emailTo(),
                    request.type().getSubject(),
                    request.type().buildBody(request.payload())
            );
        }
    }


    public void markAsRead(UUID notificationId, UUID userId) {
        var notification = getNotificationOrThrow(notificationId, userId);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void delete(UUID notificationId, UUID userId) {
        var notification = getNotificationOrThrow(notificationId, userId);
        notificationRepository.delete(notification);
    }


    // -------------------------------------------


    private void sendWebSocket(Notification notification,UserPrincipal user) {
        messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                WS_DEST,
                notification
        );
    }

    private Notification getNotificationOrThrow(UUID notificationId, UUID userId) {
        return notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification %s not found for user %s".formatted(notificationId, userId)
                ));
    }

}
