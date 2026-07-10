package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelState;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ParcelSummary(
        UUID parcelId,
        BigDecimal weightKg,
        ParcelSize size,
        boolean fragile,
        String pickupCity,
        String dropoffCity,
        ParcelState state,
        ImageDto thumbnail,
        OffsetDateTime publishedAt
) {}
