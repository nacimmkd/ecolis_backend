package com.deliveryplatform.notifications;


import com.deliveryplatform.notifications.channels.ChannelType;
import com.deliveryplatform.users.events.EmailVerificationEvent;
import com.deliveryplatform.users.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onUserVerification(EmailVerificationEvent event) {
        notificationService.notify(
                NotificationPayload.builder()
                        .receiverId(null)
                        .receiverEmail(event.userEmail())
                        .notificationType(NotificationType.VERIFY_USER)
                        .channels(Set.of(ChannelType.EMAIL))
                        .referenceId(null)
                        .persist(false)
                        .metadata(Map.of(
                                "code", event.code(),
                                "firstName" , event.firstName()
                        ))
                        .build()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onUserCreated(UserCreatedEvent event) {
        notificationService.notify(
                NotificationPayload.builder()
                        .receiverId(null)
                        .receiverEmail(event.userEmail())
                        .notificationType(NotificationType.USER_CREATED)
                        .channels(Set.of(ChannelType.EMAIL))
                        .referenceId(null)
                        .persist(false)
                        .metadata(Map.of("firstName", event.firstName()))
                        .build()
        );
    }

}