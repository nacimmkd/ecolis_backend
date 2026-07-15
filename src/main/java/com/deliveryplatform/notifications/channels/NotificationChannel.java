package com.deliveryplatform.notifications.channels;

import com.deliveryplatform.notifications.NotificationEvent;

public interface NotificationChannel {
    void send(NotificationEvent payload);
    ChannelType type();
}
