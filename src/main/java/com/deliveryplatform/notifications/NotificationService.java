package com.deliveryplatform.notifications;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.notifications.email.Email;
import com.deliveryplatform.notifications.email.EmailNotifier;
import com.deliveryplatform.notifications.email.EmailTemplates;
import com.deliveryplatform.notifications.websocket.WebSocketNotifier;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private static final String WS_DEST = "/queue/notifications";

    private final NotificationRepository notificationRepository;
    private final WebSocketNotifier webSocketNotifier;
    private final EmailNotifier emailService;


    @Transactional
    public void notify(NotificationRequest request, UserPrincipal user) {
        var notification = request.toEntity();
        notificationRepository.save(notification);

        webSocketNotifier.send(user.getUsername(), notification);

        if(request.emailTo() != null) {
            emailService.send(
                    Email.create(request.emailTo(), request.type(), request.payload())
            );
        }
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
