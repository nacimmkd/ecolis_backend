package com.deliveryplatform.notifications;


import com.deliveryplatform.notifications.channels.ChannelType;
import lombok.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationPayload {

    private UUID receiverId;
    private String receiverEmail;
    private NotificationType notificationType;
    private Set<ChannelType> channels;
    private UUID referenceId;
    private Map<String, Object> metadata;

}
