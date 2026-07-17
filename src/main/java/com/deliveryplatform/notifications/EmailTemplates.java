package com.deliveryplatform.notifications;

public final class EmailTemplates {

    private EmailTemplates() {}

    public record EmailTemplate(String subject, String body) {}

    public static EmailTemplate resolve(NotificationEvent event) {
        return switch (event.getNotificationType()) {
            case USER_CREATED -> welcomeTemplate();
            case VERIFY_USER -> confirmEmailTemplate(event);
            case BOOKING_CREATED -> bookingCreatedTemplate(event);
            case BOOKING_CANCELED -> bookingCanceledTemplate(event);
            case BOOKING_COMPLETED -> bookingCompletedTemplate(event);
            case MESSAGE_RECEIVED -> messageReceivedTemplate(event);
            case REQUEST_RECEIVED -> requestReceivedTemplate(event);
            default -> notificationReminderTemplate();
        };
    }

    private static EmailTemplate bookingCreatedTemplate(NotificationEvent event) {
        return new EmailTemplate(
                "ecolis - Réservation confirmée ✅",
                """
                Bonne nouvelle ! Votre réservation a été confirmée.

                Référence de la réservation : %s

                Vous pouvez suivre l'évolution de votre envoi ou trajet directement depuis votre espace personnel.

                À bientôt,
                L'équipe ecolis
                """.formatted(event.getReferenceId())
        );
    }

    private static EmailTemplate bookingCanceledTemplate(NotificationEvent event) {
        return new EmailTemplate(
                "ecolis - Réservation annulée ❌",
                """
                Nous vous informons que votre réservation a été annulée.

                Référence de la réservation : %s

                Si un paiement a été effectué, le remboursement sera traité selon nos conditions. Connectez-vous à votre compte pour plus de détails.

                À bientôt,
                L'équipe ecolis
                """.formatted(event.getReferenceId())
        );
    }

    private static EmailTemplate bookingCompletedTemplate(NotificationEvent event) {
        return new EmailTemplate(
                "ecolis - Colis livré ! 📦",
                """
                Mission accomplie ! Le colis est bien arrivé à destination.

                Référence de la réservation : %s

                Merci de faire vivre la communauté ecolis. N'oublie pas de laisser un avis sur cette expérience !

                À bientôt,
                L'équipe ecolis
                """.formatted(event.getReferenceId())
        );
    }

    private static EmailTemplate notificationReminderTemplate() {
        return new EmailTemplate(
                "ecolis - vous avez une nouvelle notification",
                """
                Vous avez reçu une nouvelle notification sur votre compte ecolis.

                Connectez-vous pour consulter la notification.

                À bientôt,
                L'équipe ecolis
                """
        );
    }

    private static EmailTemplate confirmEmailTemplate(NotificationEvent event) {
        return new EmailTemplate(
                "ecolis - confirmer votre email",
                """
                Merci d'avoir inscrit sur ecolis.

                Votre code de confirmation est :

                %s

                Ce code est valable 5 minutes.
                Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.

                À bientôt,
                L'équipe ecolis
                """.formatted(event.getPayload().get("code"))
        );
    }

    private static EmailTemplate welcomeTemplate() {
        return new EmailTemplate(
                "Bienvenue sur ecolis 🎉",
                """
                Ton compte ecolis est prêt !

                Tu fais maintenant partie d'une communauté qui simplifie l'envoi de colis grâce au covoiturage. Voici ce que tu peux faire dès maintenant :

                📦 Envoyez vos colis en toute sécurité, à faible coût et en pensant à la planète
                🚗 Profitez de vos trajets pour gagner de l'argent en transportant les colis des autres

                On est ravis de t'avoir parmi nous !

                À bientôt,
                L'équipe ecolis
                """
        );
    }

    private static EmailTemplate messageReceivedTemplate(NotificationEvent event) {
        var senderName = event.getPayload().get("senderName");
        var unreadCount = event.getPayload().get("unreadCount");

        return new EmailTemplate(
                "ecolis - Nouveau message 💬",
                """
                Vous avez reçu %s nouveau(x) message(s) de %s.
    
                Connectez-vous à votre espace personnel pour consulter la conversation et répondre.
    
                À bientôt,
                L'équipe ecolis
                """.formatted(unreadCount, senderName)
        );
    }

    private static EmailTemplate requestReceivedTemplate(NotificationEvent event) {
        var departureCity = event.getPayload().get("departureCity");
        var arrivalCity = event.getPayload().get("arrivalCity");

        return new EmailTemplate(
                "ecolis - Nouvelle demande reçue 📩",
                """
                Vous avez reçu une nouvelle demande pour le trajet %s → %s.
    
                Référence de la demande : %s
    
                Connectez-vous à votre espace personnel pour la consulter et y répondre.
    
                À bientôt,
                L'équipe ecolis
                """.formatted(departureCity, arrivalCity, event.getReferenceId())
        );
    }
}