package com.deliveryplatform.notifications;


import com.deliveryplatform.notifications.channels.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationManager {

    private final Set<NotificationChannel> channels;

    public void send(NotificationPayload payload) {
        channels.stream()
                .filter(channel -> payload.getChannels().contains(channel))
                .forEach(channel -> channel.send(payload));
    }

}
