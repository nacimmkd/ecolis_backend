package com.deliveryplatform.notifications;


import com.deliveryplatform.requests.events.RequestAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationService notificationService;

//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    @Async
//    public void onBookingRequested(RequestAcceptedEvent event) {
//        notificationService.notify(
//                NotificationPayload.builder()
//                        .receiverId(event.carrierId())
//                        .receiverEmail(event.carrierEmail())
//                        .notificationType(NotificationType.BOOKING_REQUESTED)
//                        .referenceId(event.bookingId())
//                        .metadata(Map.of())
//                        .build()
//        );
//    }

//    @EventListener
//    @Async
//    public void onBookingAccepted(BookingCreatedEvent event) {
//        notificationService.notify(
//                NotificationPayload.builder()
//                        .receiverId(event.senderId())
//                        .receiverEmail(event.senderEmail())
//                        .notificationType(NotificationType.BOOKING_ACCEPTED)
//                        .referenceId(event.bookingId())
//                        .build()
//        );
//    }

}