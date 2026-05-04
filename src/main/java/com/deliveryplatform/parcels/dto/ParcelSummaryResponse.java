package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ParcelSummaryResponse(
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
) {

    public ParcelSummaryResponse withOwner(ProfileSummary owner) {
        return new ParcelSummaryResponse(parcelId, owner, weightKg, size, fragile,
                pickupAddress, dropoffAddress, status, thumbnailImageUrl, createdAt);
    }

    public ParcelSummaryResponse withThumbnailImageUrl(String thumbnailImageUrl) {
        return new ParcelSummaryResponse(parcelId, owner, weightKg, size, fragile,
                pickupAddress, dropoffAddress, status, thumbnailImageUrl, createdAt);
    }
}
