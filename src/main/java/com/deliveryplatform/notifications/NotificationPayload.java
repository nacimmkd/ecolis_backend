package com.deliveryplatform.notifications;


import com.deliveryplatform.notifications.channels.ChannelType;
import lombok.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Builder
public record NotificationPayload(
        UUID receiverId,
        String receiverEmail,
        NotificationType notificationType,
        Set<ChannelType> channels,
        UUID referenceId,
        Map<String, Object> metadata
) {}
