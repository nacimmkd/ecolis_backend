package com.deliveryplatform.caching;

import java.time.Duration;

public interface CachingService {
    void save(String key, String value, Duration ttl);
    boolean isValid(String key, String value);
    boolean exists(String key);
    void remove(String key);
}
