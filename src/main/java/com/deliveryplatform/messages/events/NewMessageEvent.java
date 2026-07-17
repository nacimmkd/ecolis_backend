package com.deliveryplatform.messages.events;

import com.deliveryplatform.notifications.NotificationEvent;
import com.deliveryplatform.notifications.NotificationType;
import com.deliveryplatform.notifications.channels.ChannelType;
import com.deliveryplatform.users.User;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record NewMessageEvent(
        UUID conversationId,
        User receiver,
        String senderName,
        int unreadCount
) implements NotificationEvent {

    @Override
    public User getReceiver() {
        return receiver;
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.MESSAGE_RECEIVED;
    }

    @Override
    public Set<ChannelType> getChannels() {
        return Set.of(ChannelType.EMAIL);
    }

    @Override
    public UUID getReferenceId() {
        return conversationId;
    }

    @Override
    public Map<String, Object> getPayload() {
        return Map.of(
                "senderName", senderName,
                "unreadCount", unreadCount
        );
    }
}