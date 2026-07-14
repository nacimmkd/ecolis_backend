package com.deliveryplatform.notifications.emails;

import com.deliveryplatform.notifications.NotificationPayload;

public final class Templates {

    private Templates() {}

    public record EmailTemplate(String subject, String body) {}

    public static EmailTemplate resolve(NotificationPayload payload) {
        return switch (payload.notificationType()) {
            case USER_CREATED -> welcomeTemplate(payload);
            case VERIFY_USER -> confirmEmailTemplate(payload);
            default -> notificationReminderTemplate();
        };
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

    private static EmailTemplate confirmEmailTemplate(NotificationPayload payload) {
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
                """.formatted(payload.metadata().get("code"))
        );
    }

    private static EmailTemplate welcomeTemplate(NotificationPayload payload) {
        String firstName = (String) payload.metadata().getOrDefault("firstName", "");

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
}