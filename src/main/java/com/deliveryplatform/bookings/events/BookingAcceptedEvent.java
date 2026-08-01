package com.deliveryplatform.bookings.events;

import com.deliveryplatform.notifications.NotificationEvent;
import com.deliveryplatform.notifications.NotificationType;
import com.deliveryplatform.notifications.channels.ChannelType;
import com.deliveryplatform.users.User;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record BookingAcceptedEvent(
        UUID bookingId,
        User sender
) implements NotificationEvent {

    @Override
    public User getReceiver() {
        return sender;
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.BOOKING_CREATED;
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
