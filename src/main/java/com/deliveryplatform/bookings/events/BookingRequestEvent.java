package com.deliveryplatform.bookings.events;

import com.deliveryplatform.notifications.NotificationEvent;
import com.deliveryplatform.notifications.NotificationType;
import com.deliveryplatform.notifications.channels.ChannelType;
import com.deliveryplatform.users.User;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record BookingRequestEvent(
        UUID bookingId,
        UUID tripId,
        User receiver,
        String departureCity,
        String arrivalCity

) implements NotificationEvent {

    @Override
    public User getReceiver() {
        return receiver;
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.REQUEST_RECEIVED;
    }

    @Override
    public Set<ChannelType> getChannels() {
        return Set.of(ChannelType.EMAIL, ChannelType.IN_APP);
    }

    @Override
    public UUID getReferenceId() {
        return tripId;
    }

    @Override
    public Map<String, Object> getPayload() {
        return Map.of(
                "departure", departureCity,
                "arrival", arrivalCity
        );
    }
}
