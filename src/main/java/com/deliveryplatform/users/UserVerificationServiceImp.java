package com.deliveryplatform.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserVerificationServiceImp implements UserVerificationService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "email:verify:";
    private static final Duration TTL = Duration.ofMinutes(5);

    @Override
    public void send(String email, String code) {
        var key = PREFIX + email;
        redisTemplate.opsForValue().set(key, code, TTL);
    }

    @Override
    public boolean verify(String email, String code) {
        var key = PREFIX + email;
        String stored = redisTemplate.opsForValue().get(key);
        if (stored != null && stored.equals(code)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean exists(String email) {
        return redisTemplate.hasKey(PREFIX + email);
    }
}
