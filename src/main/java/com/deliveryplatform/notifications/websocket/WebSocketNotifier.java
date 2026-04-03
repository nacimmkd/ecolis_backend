package com.deliveryplatform.notifications.websocket;

import com.deliveryplatform.notifications.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketNotifier {

    private static final String WS_DEST = "/queue/notifications";
    private final SimpMessagingTemplate messagingTemplate;

    public void send(String username, Notification notification) {
        try{
            messagingTemplate.convertAndSendToUser(
                    username,
                    WS_DEST,
                    notification
            );
        }catch (Exception e){
            log.error("[WS] Failed to send notification — user={}", username, e);
        }
    }
}