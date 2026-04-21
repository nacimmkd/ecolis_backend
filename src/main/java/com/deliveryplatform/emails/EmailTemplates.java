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

    public static EmailContent confirmEmailTemplate(String code) {
        return new EmailContent(
                "Confirmer votre email",
                """
                Bonjour,
    
                Merci d'avoir inscrit sur DeliveryPlatform.
    
                Votre code de confirmation est :
    
                %s
    
                Ce code est valable 15 minutes.
                Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.
    
                À bientôt,
                L'équipe DeliveryPlatform
                """.formatted(code)
        );
    }
}