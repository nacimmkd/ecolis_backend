package com.deliveryplatform.notifications.emails;

public interface EmailService {
    void send(String to, String subject, String body);
}
