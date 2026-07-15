package com.deliveryplatform.notifications.channels;


import com.deliveryplatform.notifications.EmailTemplates;
import com.deliveryplatform.notifications.NotificationEvent;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@Slf4j
@RequiredArgsConstructor
class EmailNotificationChannel implements NotificationChannel {

    @Value("${resend.from-email}")
    private String from;

    @Value("${app.frontend-url}")
    private String frontUrl;

    private final Resend resendClient;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void send(NotificationEvent event) {

        var receiver = event.getUser();
        var firstName = receiver.getProfile() == null ? "" : receiver.getProfile().getFirstName();

        if (receiver.getEmail() == null) {
            throw new IllegalArgumentException("receiver email is required for EMAIL notifications");
        }

        var template = EmailTemplates.resolve(event);
        var to = receiver.getEmail();
        var subject = template.subject();
        var body = template.body();

        try {
            var params = CreateEmailOptions.builder()
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .html(resolveHTMLTemplate(body, firstName))
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

    @Override
    public ChannelType type() {
        return ChannelType.EMAIL;
    }

}
