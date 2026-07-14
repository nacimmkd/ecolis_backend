package com.deliveryplatform.notifications.emails.resend;

import com.deliveryplatform.notifications.emails.EmailService;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@Slf4j
@RequiredArgsConstructor
public class ResendEmailService  implements EmailService {

    @Value("${resend.from-email}")
    private String from;

    private final Resend resendClient;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void send(String to, String subject, String content) {
        try {
            var params = CreateEmailOptions.builder()
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .html(resolveTemplate(content))
                    .build();

            var response = resendClient.emails().send(params);
            log.info("[Email] Sent — to={} subject={} id={}", to, subject, response.getId());
        } catch (Exception e) {
            log.error("[Email] Failed — to={} subject={}", to, subject, e);
        }
    }


    private String resolveTemplate(String content) {
        var context = new Context();
        context.setVariable("content", content);
        return templateEngine.process("email", context);
    }
}
