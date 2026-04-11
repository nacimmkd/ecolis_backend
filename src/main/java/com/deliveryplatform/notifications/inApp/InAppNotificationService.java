package com.deliveryplatform.notifications.inApp;

import com.deliveryplatform.notifications.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InAppNotificationService {

    private static final String WS_DEST = "/queue/notifications";
    private final SimpMessagingTemplate messagingTemplate;

    public void send(Notification notification) {
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
}