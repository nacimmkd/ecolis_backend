package com.deliveryplatform.notifications;

import com.deliveryplatform.notifications.channels.ChannelType;
import com.deliveryplatform.users.User;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Builder(access = AccessLevel.PACKAGE)
public record NotificationPayload(
        User user,
        NotificationType notificationType,
        Set<ChannelType> channels,
        UUID referenceId,
        boolean persist,
        Map<String, Object> metadata
) {
}
