package com.deliveryplatform.notifications.channels;


import com.deliveryplatform.notifications.EmailService;
import com.deliveryplatform.notifications.EmailTemplates;
import com.deliveryplatform.notifications.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
class EmailNotificationChannel implements NotificationChannel {

    private final EmailService emailService;

    @Async
    @Override
    public void send(NotificationEvent event) {

        var receiver = event.getReceiver();
        var template = EmailTemplates.resolve(event);
        var firstName = receiver.getProfile() == null ? "" : receiver.getProfile().getFirstName();

        emailService.send(
                receiver.getEmail(),
                template.subject(),
                template.body(),
                firstName
        );
    }

    @Override
    public ChannelType type() {
        return ChannelType.EMAIL;
    }

}
