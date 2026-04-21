package com.deliveryplatform.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserVerificationServiceImp implements UserVerificationService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "email:verify:";
    private static final Duration TTL = Duration.ofMinutes(15);

    @Override
    public void save(String email, String code) {
        var key = PREFIX + email;
        redisTemplate.opsForValue().set(key, code, TTL);
    }

    @Override
    public boolean verify(String email, String code) {
        var key = PREFIX + email;
        String stored = redisTemplate.opsForValue().get(key);
        if (!Objects.isNull(stored) && stored.equals(code)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }


    @Override
    public void invalidate(String email) {
        var key = PREFIX + email;
        redisTemplate.delete(key);
    }
}
