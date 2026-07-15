package com.deliveryplatform.notifications;


import com.deliveryplatform.notifications.channels.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
class NotificationManager {

    private final Set<NotificationChannel> channels;

    public void send(NotificationEvent event) {
        channels.stream()
                .filter(channel -> event.getChannels().contains(channel.type()))
                .forEach(channel -> channel.send(event));
    }

}
