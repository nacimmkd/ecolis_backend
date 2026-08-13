package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelState;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ParcelSummary(
        UUID parcelId,
        String title,
        BigDecimal weightKg,
        ParcelSize size,
        boolean fragile,
        Address pickup,
        Address dropoff,
        ParcelState state,
        List<ImageDto> images,
        OffsetDateTime publishedAt
) {}
