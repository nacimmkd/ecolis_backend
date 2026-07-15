package com.deliveryplatform.notifications;

import com.deliveryplatform.notifications.channels.ChannelType;
import com.deliveryplatform.users.User;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface NotificationEvent {
    User getUser();
    NotificationType getNotificationType();
    Set<ChannelType> getChannels();
    UUID getReferenceId();
    Map<String, Object> getPayload();
}