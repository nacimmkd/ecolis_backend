package com.deliveryplatform.notifications;

import lombok.Getter;

import java.util.Map;


@Getter
public enum NotificationType {

    BOOKING_REQUESTED( "Nouvelle demande de livraison", true) {
        @Override
        public String buildBody(Map<String, Object> p) {
            return EmailTemplates.bookingRequestedBody(p);
        }
    },

    BOOKING_ACCEPTED("Votre demande a été acceptée", true) {
        @Override
        public String buildBody(Map<String, Object> p) {
            return EmailTemplates.bookingAcceptedBody(p);
        }
    };



    // ----------------------------------------------
    private final String subject;
    private final boolean sendEmail;

    NotificationType(String subject, boolean sendEmail) {
        this.subject = subject;
        this.sendEmail = sendEmail;
    }

    public abstract String buildBody(Map<String, Object> payload);


    }
