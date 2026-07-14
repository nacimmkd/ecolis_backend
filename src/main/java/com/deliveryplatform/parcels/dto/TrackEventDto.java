package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.parcels.ParcelState;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record TrackEventDto(
        UUID id,
        ParcelState state,
        String note,
        OffsetDateTime occurredAt
) {
}
