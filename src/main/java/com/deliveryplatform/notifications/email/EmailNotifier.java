package com.deliveryplatform.notifications.email;

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
public class EmailNotifier {

    @Value("${spring.mail.username}")
    private  String from;

    private final JavaMailSender mailSender;

    @Async
    public void send(Email email) {
        try {
            var message = buildMailMessage(email.getTo(), email.getSubject(), email.getBody());
            mailSender.send(message);
            log.info("[Email] Sent — to={} subject={}",email.getTo(), email.getSubject());
        } catch (Exception e) {
            log.error("[Email] Failed — to={} subject={}", email.getTo(), email.getSubject(), e);
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
