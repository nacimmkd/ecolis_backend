package com.deliveryplatform.users;

import com.deliveryplatform.common.caching.CachingService;
import com.deliveryplatform.users.exceptions.UserErrorCode;
import com.deliveryplatform.users.exceptions.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class TokenService {

    private final CachingService cachingService;
    private static final Duration VERIFICATION_TOKEN_TTL = Duration.ofMinutes(30);

    /*
    *  key = generated token
    *  value = user.id
    */

    public String generateAndSave(User user) {
        var token = UUID.randomUUID().toString();
        var userId = user.getId().toString();
        cachingService.save(token, userId, VERIFICATION_TOKEN_TTL);
        return token;
    }

    public UUID verifyToken(String token) {
        var userId = cachingService.get(token); // value
        if (userId == null) {
            throw new UserException(UserErrorCode.INVALID_VERIFICATION_TOKEN, "Token invalid or expired");
        }
        cachingService.remove(token);

        return UUID.fromString(userId);
    }
}