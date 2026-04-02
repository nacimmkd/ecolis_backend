package com.deliveryplatform.notifications;

import java.util.Map;

public final class EmailTemplates {

    private EmailTemplates() {}

    public static String bookingRequestedBody(Map<String, Object> p) {
        return """
                Vous avez une nouvelle demande de livraison pour votre trajet %s → %s.
                Connectez-vous pour accepter ou refuser.
                """
                .formatted(p.get("departureCity"), p.get("arrivalCity"));
    }

    public static String bookingAcceptedBody(Map<String, Object> p) {
        return """
                %s a accepté votre colis.
                Procédez au paiement de %s€ pour confirmer votre réservation.
                """
                .formatted(p.get("carrierName"), p.get("price"));
    }
}
