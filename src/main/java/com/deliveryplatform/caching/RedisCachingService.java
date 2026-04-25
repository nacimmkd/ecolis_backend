package com.deliveryplatform.caching;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisCachingService implements CachingService {

    private final RedisTemplate<String,String> redisTemplate;

    @Override
    public void save(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(
                key,
                value,
                ttl
        );
    }

    @Override
    public boolean isValid(String key, String value) {
        String stored = redisTemplate.opsForValue().get(key);
        return stored != null && stored.equals(value);
    }

    @Override
    public boolean exists(String key) {
        String stored = redisTemplate.opsForValue().get(key);
        return stored != null;
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }
}
