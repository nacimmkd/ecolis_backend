package com.deliveryplatform.notifications.events;

import com.deliveryplatform.notifications.NotificationServiceImp;
import com.deliveryplatform.notifications.NotificationType;
import com.deliveryplatform.notifications.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationServiceImp notificationServiceImp;

    @EventListener
    @Async
    public void onBookingRequested(BookingRequestedEvent event) {
        notificationServiceImp.notify(
                NotificationRequest.builder()
                        .userId(event.carrierId())
                        .type(NotificationType.BOOKING_REQUESTED)
                        .referenceId(event.bookingId())
                        .emailTo(event.carrierEmail())
                        .build()
        );
    }

    @EventListener
    @Async
    public void onBookingAccepted(BookingAcceptedEvent event) {
        notificationServiceImp.notify(
                NotificationRequest.builder()
                        .userId(event.senderId())
                        .type(NotificationType.BOOKING_ACCEPTED)
                        .referenceId(event.bookingId())
                        .emailTo(event.senderEmail())
                        .build()
        );
    }

    @EventListener
    @Async
    public void onBookingCancelled(BookingCancelledEvent event) {
        notificationServiceImp.notify(
                NotificationRequest.builder()
                        .userId(event.userId())
                        .type(NotificationType.BOOKING_CANCELLED)
                        .referenceId(event.bookingId())
                        .emailTo(event.userEmail())
                        .build()
        );
    }

    @EventListener
    @Async
    public void onPaymentReceived(PaymentReceivedEvent event) {
        notificationServiceImp.notify(
                NotificationRequest.builder()
                        .userId(event.userId())
                        .type(NotificationType.PAYMENT_RECEIVED)
                        .referenceId(event.bookingId())
                        .emailTo(event.userEmail())
                        .build()
        );
    }

    @EventListener
    @Async
    public void onTripCancelled(TripCancelledEvent event) {
        notificationServiceImp.notify(
                NotificationRequest.builder()
                        .userId(event.userId())
                        .type(NotificationType.TRIP_CANCELLED)
                        .referenceId(event.tripId())
                        .emailTo(event.userEmail())
                        .build()
        );
    }

    @EventListener
    @Async
    public void onParcelDelivered(ParcelDeliveredEvent event) {
        notificationServiceImp.notify(
                NotificationRequest.builder()
                        .userId(event.userId())
                        .type(NotificationType.PARCEL_DELIVERED)
                        .referenceId(event.parcelId())
                        .emailTo(event.userEmail())
                        .build()
        );
    }
}