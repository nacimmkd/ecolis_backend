package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.parcels.ParcelStatus;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record TrackEventDto(
        UUID id,
        ParcelStatus status,
        String note,
        OffsetDateTime occurredAt
) {
}
