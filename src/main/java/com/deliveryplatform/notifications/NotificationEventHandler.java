package com.deliveryplatform.notifications;

import com.deliveryplatform.notifications.events.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationService notificationService;

    @EventListener
    @Async
    public void onBookingRequested(BookingRequestedEvent event) {
        notificationService.notify(
                NotificationRequest.builder()
                        .userId(event.carrierId())
                        .type(NotificationType.BOOKING_REQUESTED)
                        .referenceId(event.bookingId())
                        .emailTo(event.carrierEmail())
                        .payload(new NotificationPayloads.BookingRequested(
                                event.departureCity(),
                                event.arrivalCity()
                        ))
                        .build()
        );
    }

    @EventListener
    @Async
    public void onBookingAccepted(BookingAcceptedEvent event) {
        notificationService.notify(
                NotificationRequest.builder()
                        .userId(event.senderId())
                        .type(NotificationType.BOOKING_ACCEPTED)
                        .referenceId(event.bookingId())
                        .emailTo(event.senderEmail())
                        .payload(new NotificationPayloads.BookingAccepted(
                                event.carrierName(),
                                event.price()
                        ))
                        .build()
        );
    }

    @EventListener
    @Async
    public void onBookingCancelled(BookingCancelledEvent event) {
        notificationService.notify(
                NotificationRequest.builder()
                        .userId(event.userId())
                        .type(NotificationType.BOOKING_CANCELLED)
                        .referenceId(event.bookingId())
                        .emailTo(event.userEmail())
                        .payload(new NotificationPayloads.BookingCancelled(event.bookingId()))
                        .build()
        );
    }

    @EventListener
    @Async
    public void onPaymentReceived(PaymentReceivedEvent event) {
        notificationService.notify(
                NotificationRequest.builder()
                        .userId(event.userId())
                        .type(NotificationType.PAYMENT_RECEIVED)
                        .referenceId(event.bookingId())
                        .emailTo(event.userEmail())
                        .payload(new NotificationPayloads.PaymentReceived(
                                event.amount(),
                                event.bookingId()
                        ))
                        .build()
        );
    }

    @EventListener
    @Async
    public void onTripCancelled(TripCancelledEvent event) {
        notificationService.notify(
                NotificationRequest.builder()
                        .userId(event.userId())
                        .type(NotificationType.TRIP_CANCELLED)
                        .referenceId(event.tripId())
                        .emailTo(event.userEmail())
                        .payload(new NotificationPayloads.TripCancelled(
                                event.departureCity(),
                                event.arrivalCity(),
                                event.departureDate()
                        ))
                        .build()
        );
    }

    @EventListener
    @Async
    public void onParcelDelivered(ParcelDeliveredEvent event) {
        notificationService.notify(
                NotificationRequest.builder()
                        .userId(event.userId())
                        .type(NotificationType.PARCEL_DELIVERED)
                        .referenceId(event.parcelId())
                        .emailTo(event.userEmail())
                        .payload(new NotificationPayloads.ParcelDelivered(event.parcelId()))
                        .build()
        );
    }
}