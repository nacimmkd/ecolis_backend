package com.deliveryplatform.payments.events;

import com.deliveryplatform.notifications.NotificationEvent;
import com.deliveryplatform.notifications.NotificationType;
import com.deliveryplatform.notifications.channels.ChannelType;
import com.deliveryplatform.users.User;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record PaymentAuthorizedEvent(UUID bookingId, User payer) implements NotificationEvent {

    @Override
    public User getReceiver() {
        return payer;
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.BOOKING_PAID;
    }

    @Override
    public Set<ChannelType> getChannels() {
        return Set.of(ChannelType.EMAIL, ChannelType.IN_APP);
    }

    @Override
    public UUID getReferenceId() {
        return bookingId;
    }

    @Override
    public Map<String, Object> getPayload() {
        return Map.of();
    }
}
