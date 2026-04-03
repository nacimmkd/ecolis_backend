package com.deliveryplatform.notifications;

import lombok.Getter;


@Getter
public enum NotificationType {

    BOOKING_REQUESTED("Nouvelle demande de livraison", NotificationPayloads.BookingRequested.class),
    BOOKING_ACCEPTED("Votre demande a été acceptée",  NotificationPayloads.BookingAccepted.class),
    BOOKING_REJECTED("Votre demande a été refusée",  NotificationPayloads.BookingRejected.class),
    BOOKING_CANCELLED("Votre réservation a été annulée",  NotificationPayloads.BookingCancelled.class),

    PAYMENT_RECEIVED("Paiement reçu",  NotificationPayloads.PaymentReceived.class),
    PAYMENT_FAILED("Échec du paiement",  NotificationPayloads.PaymentFailed.class),

    TRIP_STARTED("Votre trajet a démarré",  Void.class),
    TRIP_COMPLETED("Votre trajet est terminé",  Void.class),
    TRIP_CANCELLED("Votre trajet a été annulé",  NotificationPayloads.TripCancelled.class),

    PARCEL_PICKED_UP("Votre colis a été pris en charge",  Void.class),
    PARCEL_DELIVERED("Votre colis a été livré", NotificationPayloads.ParcelDelivered.class);

    private final String subject;
    private final Class<?> payloadType;

    NotificationType(String subject, Class<?> payloadType) {
        this.subject = subject;
        this.payloadType = payloadType;
    }
}