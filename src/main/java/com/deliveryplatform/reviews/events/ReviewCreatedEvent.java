package com.deliveryplatform.reviews.events;

import com.deliveryplatform.notifications.NotificationEvent;
import com.deliveryplatform.notifications.NotificationType;
import com.deliveryplatform.notifications.channels.ChannelType;
import com.deliveryplatform.notifications.channels.NotificationChannel;
import com.deliveryplatform.users.User;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record ReviewCreatedEvent(User reviewee, UUID reviewId) implements NotificationEvent {
    @Override
    public User getReceiver() {
        return reviewee;
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.REVIEW_CREATED;
    }

    @Override
    public Set<ChannelType> getChannels() {
        return Set.of(ChannelType.IN_APP);
    }

    @Override
    public UUID getReferenceId() {
        return reviewId;
    }

    @Override
    public Map<String, Object> getPayload() {
        return Map.of();
    }
}