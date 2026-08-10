package com.deliveryplatform.users.events;

import com.deliveryplatform.notifications.NotificationEvent;
import com.deliveryplatform.notifications.NotificationType;
import com.deliveryplatform.notifications.channels.ChannelType;
import com.deliveryplatform.users.User;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record PasswordResetEvent(
        User user,
        String url

) implements NotificationEvent {

    @Override
    public User getReceiver() {
        return user;
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.RESET_PASSWORD;
    }

    @Override
    public Set<ChannelType> getChannels() {
        return Set.of(ChannelType.EMAIL);
    }

    @Override
    public UUID getReferenceId() {
        return null;
    }

    @Override
    public Map<String, Object> getPayload() {
        return Map.of("url", url);
    }
}
