package com.deliveryplatform.emails;

public final class EmailTemplates {

    private EmailTemplates() {}

    public record EmailContent(String subject, String body) {}

    public static EmailContent notificationReminderTemplate() {
        return new EmailContent(
                "Vous avez une nouvelle notification",
                """
                Bonjour,

                Vous avez reçu une nouvelle notification sur votre compte DeliveryPlatform.

                Connectez-vous pour consulter la notification.

                À bientôt,
                L'équipe DeliveryPlatform
                """
        );
    }
}