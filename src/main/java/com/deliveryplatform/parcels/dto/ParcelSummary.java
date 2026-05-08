package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record ParcelSummary(
        UUID parcelId,
        ProfileSummary owner,
        BigDecimal weightKg,
        ParcelSize size,
        boolean fragile,
        GeocodedAddress pickupAddress,
        GeocodedAddress dropoffAddress,
        ParcelStatus status,
        String thumbnailImageUrl,
        OffsetDateTime createdAt
) {}
