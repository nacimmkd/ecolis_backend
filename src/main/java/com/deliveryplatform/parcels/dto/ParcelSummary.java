package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.users.dto.UserSummary;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ParcelSummary(
        UUID parcelId,
        UserSummary owner,
        BigDecimal weightKg,
        ParcelSize size,
        boolean fragile,
        GeocodedAddress pickupAddress,
        GeocodedAddress dropoffAddress,
        ParcelStatus status,
        ImageDto thumbnailImage,
        OffsetDateTime createdAt
) {}
