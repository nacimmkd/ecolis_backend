package com.deliveryplatform.common.caching;

import java.time.Duration;

public interface CachingService {
    void save(String key, String value, Duration ttl);
    String get(String key);
    boolean isValid(String key, String value);
    boolean exists(String key);
    void remove(String key);
}
