package com.deliveryplatform.notifications.channels;

import com.deliveryplatform.notifications.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
class InAppNotificationChannel implements NotificationChannel {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String WS_DEST = "/queue/notifications";


    @Override
    public void send(NotificationEvent event) {

        var receiver = event.getUser();

        if (receiver.getId() == null) {
            throw new IllegalArgumentException("receiverId is required for IN_APP notifications");
        }
        try{
            messagingTemplate.convertAndSendToUser(
                    receiver.getId().toString(),
                    WS_DEST,
                    event
            );
        }catch (Exception e){
            log.error("[WS] Failed to send notification — user={} — message={}", receiver.getId().toString(), e.getMessage());
        }
    }

    @Override
    public ChannelType type() {
        return ChannelType.IN_APP;
    }
}
