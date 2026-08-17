package com.deliveryplatform.parcels.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ParcelImageDto(
        UUID id,
        String url,
        String content,
        OffsetDateTime uploadedAt
) {}
