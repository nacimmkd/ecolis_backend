package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.parcels.ParcelState;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record TrackEventDto(
        UUID id,
        ParcelState status,
        String note,
        OffsetDateTime occurredAt
) {
}
