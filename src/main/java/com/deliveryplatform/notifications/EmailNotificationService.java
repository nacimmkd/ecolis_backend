package com.deliveryplatform.notifications;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationService {

    @Value("${app.mail.from}")
    private String from;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void send(String to, String subject, String content) {
        try {
            var mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(resolveTemplate(content), true);
            mailSender.send(mime);
            log.info("[Email] Sent — to={} subject={}", to, subject);
        } catch (Exception e) {
            log.error("[Email] Failed — to={} subject={}", to, subject);
        }
    }


    private String resolveTemplate(String content) {
        var context = new Context();
        context.setVariable("content", content);
        return templateEngine.process("email", context);
    }
}
