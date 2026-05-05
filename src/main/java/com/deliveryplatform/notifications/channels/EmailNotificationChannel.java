package com.deliveryplatform.notifications.channels;


import com.deliveryplatform.emails.EmailService;
import com.deliveryplatform.emails.Templates;
import com.deliveryplatform.notifications.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel {

    private EmailService emailService;

    @Override
    public void send(NotificationPayload payload) {
        var template = Templates.notificationReminderTemplate();
        emailService.send(
                payload.getReceiverEmail(),
                template.subject(),
                template.body()
        );
    }

    @Override
    public ChannelType channelType() {
        return ChannelType.EMAIL;
    }

}
