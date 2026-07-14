package com.deliveryplatform.notifications;


import com.deliveryplatform.requests.events.RequestAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

//    @EventListener
//    @Async
//    public void onBookingAccepted(RequestAcceptedEvent event) {
//        notificationService.notify(
//                NotificationPayload.builder()
//                        .receiverId(event.senderId())
//                        .receiverEmail(event.senderEmail())
//                        .notificationType(NotificationType.BOOKING_CREATED)
//                        .referenceId(event.bookingId())
//                        .build()
//        );
//    }

}