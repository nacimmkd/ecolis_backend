package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.profiles.dto.ProfileSummaryResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ParcelDetailedResponse(
        UUID id,
        ProfileSummaryResponse owner,
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

    public static ParcelDetailedResponse of(Parcel parcel, ProfileSummaryResponse owner, String thumbnailImageUrl, List<String> imageUrls) {
        return ParcelDetailedResponse.builder()
                .id(parcel.getId())
                .owner(owner)
                .description(parcel.getDescription())
                .weightKg(parcel.getWeightKg())
                .size(parcel.getSize())
                .fragile(parcel.isFragile())
                .pickupAddress(parcel.getPickupAddress())
                .dropoffAddress(parcel.getDropoffAddress())
                .status(parcel.getStatus())
                .thumbnailImageUrl(thumbnailImageUrl)
                .imageUrls(imageUrls)
                .createdAt(parcel.getCreatedAt())
                .build();
    }
}
