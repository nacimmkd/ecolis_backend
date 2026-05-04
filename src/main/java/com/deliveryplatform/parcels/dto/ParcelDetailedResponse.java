package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ParcelDetailedResponse(
        UUID parcelId,
        ProfileSummary owner,
        String description,
        BigDecimal weightKg,
        ParcelSize size,
        boolean fragile,
        GeocodedAddress pickupAddress,
        GeocodedAddress dropoffAddress,
        ParcelStatus status,
        String thumbnailImageUrl,
        List<String> imageUrls,
        OffsetDateTime createdAt
) {

    public ParcelDetailedResponse withOwner(ProfileSummary owner) {
        return new ParcelDetailedResponse(parcelId, owner, description, weightKg, size, fragile,
                pickupAddress, dropoffAddress, status, thumbnailImageUrl, imageUrls, createdAt);
    }

    public ParcelDetailedResponse withThumbnailImageUrl(String thumbnailImageUrl) {
        return new ParcelDetailedResponse(parcelId, owner, description, weightKg, size, fragile,
                pickupAddress, dropoffAddress, status, thumbnailImageUrl, imageUrls, createdAt);
    }

    public ParcelDetailedResponse withImageUrls(List<String> imageUrls) {
        return new ParcelDetailedResponse(parcelId, owner, description, weightKg, size, fragile,
                pickupAddress, dropoffAddress, status, thumbnailImageUrl, imageUrls, createdAt);
    }
}
