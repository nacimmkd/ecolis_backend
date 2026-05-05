package com.deliveryplatform.notifications.channels;

import com.deliveryplatform.notifications.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InAppNotificationChannel implements NotificationChannel {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String WS_DEST = "/queue/notifications";


    @Override
    public void send(NotificationPayload payload) {
        if (payload.getReferenceId() == null) {
            throw new IllegalArgumentException("Missing receiverId");
        }
        try{
            messagingTemplate.convertAndSendToUser(
                    payload.getReceiverId().toString(),
                    WS_DEST,
                    payload
            );
        }catch (Exception e){
            log.error("[WS] Failed to send notification — user={} — message={}", payload.getReceiverId().toString(), e.getMessage());
        }
    }

    @Override
    public ChannelType channelType() {
        return ChannelType.IN_APP;
    }
}
