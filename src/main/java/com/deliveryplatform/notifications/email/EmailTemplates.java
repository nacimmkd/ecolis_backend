package com.deliveryplatform.notifications.email;


import com.deliveryplatform.notifications.NotificationPayloads;

public final class EmailTemplates {

    private EmailTemplates() {}

    public static String bookingRequested(NotificationPayloads.BookingRequested p) {
        return """
                Vous avez une nouvelle demande de livraison pour votre trajet %s → %s.
                Connectez-vous pour accepter ou refuser.
                """.formatted(p.departureCity(), p.arrivalCity());
    }

    public static String bookingAccepted(NotificationPayloads.BookingAccepted p) {
        return """
                %s a accepté votre colis.
                Procédez au paiement de %.2f€ pour confirmer votre réservation.
                """.formatted(p.carrierName(), p.price());
    }

    public static String bookingRejected(NotificationPayloads.BookingRejected p) {
        return """
                Votre demande pour le trajet %s → %s a été refusée.
                """.formatted(p.departureCity(), p.arrivalCity());
    }

    public static String bookingCancelled(NotificationPayloads.BookingCancelled p) {
        return """
                La réservation #%s a été annulée.
                """.formatted(p.bookingId());
    }

    public static String paymentReceived(NotificationPayloads.PaymentReceived p) {
        return """
                Votre paiement de %.2f€ a bien été reçu pour la réservation #%s.
                """.formatted(p.amount(), p.bookingId());
    }

    public static String paymentFailed(NotificationPayloads.PaymentFailed p) {
        return """
                Le paiement de %.2f€ pour la réservation #%s a échoué.
                Veuillez réessayer ou contacter le support.
                """.formatted(p.amount(), p.bookingId());
    }

    public static String tripCancelled(NotificationPayloads.TripCancelled p) {
        return """
                Le trajet %s → %s prévu le %s a été annulé.
                """.formatted(p.departureCity(), p.arrivalCity(), p.departureDate());
    }

    public static String parcelDelivered(NotificationPayloads.ParcelDelivered p) {
        return """
                Votre colis #%s a été livré avec succès.
                """.formatted(p.parcelId());
    }
}