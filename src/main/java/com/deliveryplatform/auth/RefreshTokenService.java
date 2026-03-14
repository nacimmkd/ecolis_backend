package com.deliveryplatform.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtConfig jwtConfig;

    public void saveRefreshToken(UUID userId, String token) {
        var key = "refreshToken:" + userId;
        var ttl = jwtConfig.getRefreshTokenExpiration();
        redisTemplate.opsForValue().set(key, token, ttl ,TimeUnit.SECONDS );
    }

    public String getRefreshToken(UUID userId) {
        var key = "refreshToken:" + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public void removeRefreshToken(UUID userId) {
        var key = "refreshToken:" + userId;
        redisTemplate.delete(key);
    }
}
