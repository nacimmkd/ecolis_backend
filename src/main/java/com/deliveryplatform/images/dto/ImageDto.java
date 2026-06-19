package com.deliveryplatform.images.dto;

import com.deliveryplatform.storage.MediaType;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ImageDto(
        UUID id,
        String url,
        String content,
        OffsetDateTime uploadedAt
) {}
