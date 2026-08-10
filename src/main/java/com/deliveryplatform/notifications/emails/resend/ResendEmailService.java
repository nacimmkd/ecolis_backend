package com.deliveryplatform.notifications.emails.resend;

import com.deliveryplatform.notifications.EmailService;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResendEmailService implements EmailService {

    private final Resend resendClient;
    private final TemplateEngine templateEngine;

    @Value("${resend.from-email}")
    private String from;

    @Value("${front-end.base-url}")
    private String frontUrl;

    @Override
    public void send(String to, String subject, String content, String firstName) {

        if (to == null) {
            throw new IllegalArgumentException("missing email address");
        }

        try {
            String htmlBody = resolveHTMLTemplate(content, firstName);
            var params = CreateEmailOptions.builder()
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .html(htmlBody)
                    .build();

            var response = resendClient.emails().send(params);
            log.info("[Email] Sent — to={} subject={} id={}", to, subject, response.getId());
        } catch (Exception e) {
            log.error("[Email] Failed — to={} subject={}", to, subject, e);
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