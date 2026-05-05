package com.deliveryplatform.notifications.events;

import com.deliveryplatform.notifications.NotificationServiceImp;
import com.deliveryplatform.notifications.NotificationType;
import com.deliveryplatform.notifications.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationServiceImp notificationServiceImp;

    @EventListener
    @Async
    public void onBookingRequested(BookingRequestedEvent event) {
        notificationServiceImp.notify(
                NotificationPayload.builder()
                        .receiverId(event.carrierId())
                        .receiverEmail(event.carrierEmail())
                        .notificationType(NotificationType.BOOKING_REQUESTED)
                        .referenceId(event.bookingId())
                        .metadata(Map.of())
                        .build()
        );
    }

    @EventListener
    @Async
    public void onBookingAccepted(BookingAcceptedEvent event) {
        notificationServiceImp.notify(
                NotificationPayload.builder()
                        .receiverId(event.senderId())
                        .receiverEmail(event.senderEmail())
                        .notificationType(NotificationType.BOOKING_ACCEPTED)
                        .referenceId(event.bookingId())
                        .build()
        );
    }

    @EventListener
    @Async
    public void onBookingCancelled(BookingCancelledEvent event) {
        notificationServiceImp.notify(
                NotificationPayload.builder()
                        .referenceId(event.userId())
                        .receiverEmail(event.userEmail())
                        .notificationType(NotificationType.BOOKING_CANCELLED)
                        .referenceId(event.bookingId())
                        .build()
        );
    }

    @EventListener
    @Async
    public void onPaymentReceived(PaymentReceivedEvent event) {
        notificationServiceImp.notify(
                NotificationPayload.builder()
                        .receiverId(event.userId())
                        .receiverEmail(event.userEmail())
                        .notificationType(NotificationType.PAYMENT_RECEIVED)
                        .referenceId(event.bookingId())
                        .build()
        );
    }

    @EventListener
    @Async
    public void onTripCancelled(TripCancelledEvent event) {
        notificationServiceImp.notify(
                NotificationPayload.builder()
                        .receiverId(event.userId())
                        .receiverEmail(event.userEmail())
                        .notificationType(NotificationType.TRIP_CANCELLED)
                        .referenceId(event.tripId())
                        .build()
        );
    }

    @EventListener
    @Async
    public void onParcelDelivered(ParcelDeliveredEvent event) {
        notificationServiceImp.notify(
                NotificationPayload.builder()
                        .receiverId(event.userId())
                        .receiverEmail(event.userEmail())
                        .notificationType(NotificationType.PARCEL_DELIVERED)
                        .referenceId(event.parcelId())
                        .build()
        );
    }
}