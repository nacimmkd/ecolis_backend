package com.deliveryplatform.messages.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record MessageImageDto(
        UUID id,
        String url,
        String content,
        OffsetDateTime uploadedAt
) {}