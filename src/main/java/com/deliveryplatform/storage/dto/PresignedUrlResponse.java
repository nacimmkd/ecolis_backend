package com.deliveryplatform.storage.dto;

import java.time.Duration;
import java.time.Instant;

public record PresignedUrlResponse(
        String uploadUrl,
        String key,
        String contentType,
        Instant expiresAt
) {

    public static PresignedUrlResponse of(String uploadUrl, String key, String contentType, Duration duration) {
        return new PresignedUrlResponse(
                uploadUrl,
                key,
                contentType,
                Instant.now().plusSeconds(duration.toMinutes()*60)
        );
    }
}
