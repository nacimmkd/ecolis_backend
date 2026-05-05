package com.deliveryplatform.notifications.channels;

import com.deliveryplatform.notifications.NotificationPayload;

public interface NotificationChannel {
    void send(NotificationPayload payload);
    ChannelType channelType();
}
