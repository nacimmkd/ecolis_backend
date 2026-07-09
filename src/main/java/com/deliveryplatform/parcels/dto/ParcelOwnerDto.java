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
public record ParcelOwnerDto(
        UUID parcelId,
        String description,
        BigDecimal weightKg,
        ParcelSize size,
        boolean fragile,
        Address pickupAddress,
        Address dropoffAddress,
        ParcelState state,
        ImageDto thumbnail,
        List<ImageDto> images,
        OffsetDateTime createdAt
){}
