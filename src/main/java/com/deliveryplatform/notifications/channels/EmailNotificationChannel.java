package com.deliveryplatform.notifications.channels;


import com.deliveryplatform.notifications.emails.EmailService;
import com.deliveryplatform.notifications.emails.Templates;
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
                payload.receiverEmail(),
                template.subject(),
                template.body()
        );
    }

    @Override
    public ChannelType type() {
        return ChannelType.EMAIL;
    }

}
