package com.deliveryplatform.users;

import com.deliveryplatform.common.caching.CachingService;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.InvalidCredentialsException;
import com.deliveryplatform.users.events.EmailVerificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService{

    private final CachingService cachingService;
    private final ApplicationEventPublisher eventPublisher;

    private static final String VERIFICATION_CODE_PREFIX = "email:verify:";
    private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(5);


    public void sendCode(User user) {
        var key = VERIFICATION_CODE_PREFIX + user.getEmail();
        var code = CodeGeneratorUtil.generateVerificationCode();

        if (cachingService.exists(key)) {
            cachingService.remove(key);
        }
        cachingService.save(key, code, VERIFICATION_CODE_TTL);

        eventPublisher.publishEvent(new EmailVerificationEvent(user, code));
    }


    public void verifyCode(User user, String code) {
        var key = VERIFICATION_CODE_PREFIX + user.getEmail();
        if (!cachingService.isValid(key, code)) {
            throw new InvalidCredentialsException("Code invalid or expired");
        }
    }
}