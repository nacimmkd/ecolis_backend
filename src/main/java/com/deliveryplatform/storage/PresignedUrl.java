package com.deliveryplatform.storage;

import java.time.Duration;
import java.time.Instant;

public record PresignedUrl(
        String url,
        String key,
        Instant expiresAt
) {

    public static PresignedUrl of(String url, String key, Duration duration) {
        return new PresignedUrl(
                url,
                key,
                Instant.now().plusSeconds(duration.toMinutes()*60)
        );
    }
}
