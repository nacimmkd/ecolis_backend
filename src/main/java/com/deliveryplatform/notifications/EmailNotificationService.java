package com.deliveryplatform.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationService {

    @Value("${spring.mail.username}")
    private  String from;

    private final JavaMailSender mailSender;

    @Async
    public void send(String to, String subject, String body) {
        try {
            var message = buildMailMessage(to, subject, body);
            mailSender.send(message);
            log.info("[Email] Sent — to={} subject={}", to, subject);
        } catch (Exception e) {
            log.error("[Email] Failed — to={} subject={}", to, subject, e);
        }
    }


    private SimpleMailMessage buildMailMessage(String to, String subject, String body) {
        var message =  new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        return message;
    }
}
