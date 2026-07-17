package com.deliveryplatform.notifications;

public interface EmailService {
    void send(String to, String subject, String content, String firstName);
}