package com.deliveryplatform.notifications;

import lombok.Getter;


@Getter
public enum NotificationType {

    BOOKING_REQUESTED("Nouvelle demande de livraison", true, NotificationPayloads.BookingRequested.class),
    BOOKING_ACCEPTED("Votre demande a été acceptée", true, NotificationPayloads.BookingAccepted.class),
    BOOKING_REJECTED("Votre demande a été refusée", true, NotificationPayloads.BookingRejected.class),
    BOOKING_CANCELLED("Votre réservation a été annulée", true, NotificationPayloads.BookingCancelled.class),

    PAYMENT_RECEIVED("Paiement reçu", true, NotificationPayloads.PaymentReceived.class),
    PAYMENT_FAILED("Échec du paiement", true, NotificationPayloads.PaymentFailed.class),

    TRIP_STARTED("Votre trajet a démarré", false, Void.class),
    TRIP_COMPLETED("Votre trajet est terminé", false, Void.class),
    TRIP_CANCELLED("Votre trajet a été annulé", true, NotificationPayloads.TripCancelled.class),

    PARCEL_PICKED_UP("Votre colis a été pris en charge", false, Void.class),
    PARCEL_DELIVERED("Votre colis a été livré", true, NotificationPayloads.ParcelDelivered.class);

    private final String subject;
    private final boolean sendEmail;
    private final Class<?> payloadType;

    NotificationType(String subject, boolean sendEmail, Class<?> payloadType) {
        this.subject = subject;
        this.sendEmail = sendEmail;
        this.payloadType = payloadType;
    }
}