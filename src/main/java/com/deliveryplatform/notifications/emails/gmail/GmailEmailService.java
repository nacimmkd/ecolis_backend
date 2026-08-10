package com.deliveryplatform.notifications.emails.gmail;

import com.deliveryplatform.notifications.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
@Primary
public class GmailEmailService implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.username}")
    private String from;

    @Value("${front-end.base-url}")
    private String frontUrl;

    @Override
    public void send(String to, String subject, String content, String firstName) {
        try {
            String htmlBody = resolveHTMLTemplate(content, firstName);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            javaMailSender.send(message);
            log.info("[Gmail] Sent — to={} subject={}", to, subject);

        } catch (Exception e) {
            log.error("[Gmail] Failed — to={} subject={}", to, subject, e);
        }
    }

    private String resolveHTMLTemplate(String content, String firstName) {
        var context = new Context();
        context.setVariable("content", content);
        context.setVariable("firstName", firstName);
        context.setVariable("btnUrl", frontUrl);
        return templateEngine.process("email", context);
    }
}
