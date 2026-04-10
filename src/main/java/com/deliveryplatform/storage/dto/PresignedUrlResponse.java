package com.deliveryplatform.storage.dto;

import java.time.Duration;
import java.time.Instant;

public record PresignedUrlResponse(
        String uploadUrl,
        String key,
        Instant expiresAt
) {

    public static PresignedUrlResponse of(String uploadUrl, String key, Duration duration) {
        return new PresignedUrlResponse(
                uploadUrl,
                key,
                Instant.now().plusSeconds(duration.toMinutes()*60)
        );
    }
}
