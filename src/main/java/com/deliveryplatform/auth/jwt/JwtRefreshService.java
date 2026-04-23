package com.deliveryplatform.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtRefreshService {

    private final JwtConfig jwtConfig;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "REFRESH_TOKEN:";

    public void save(UUID userId, String token) {
        var key = PREFIX + userId;
        redisTemplate.opsForValue().set(
                key,
                token,
                Duration.ofSeconds(jwtConfig.getRefreshTokenDuration())
        );
    }

    public boolean isValid(UUID userId, String token) {
        String stored = redisTemplate.opsForValue().get(PREFIX + userId);
        return stored != null && stored.equals(token);
    }

    public void remove(UUID userId) {
        var key = PREFIX + userId;
        redisTemplate.delete(key);
    }
}
