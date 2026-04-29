package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.profiles.dto.UserProfileSummary;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ParcelDetailedResponse(
        UUID id,
        UserProfileSummary user,
        String description,
        BigDecimal weightKg,
        ParcelSize size,
        boolean fragile,
        boolean requireCode,
        GeocodedAddress pickupAddress,
        GeocodedAddress dropoffAddress,
        ParcelStatus status,
        String thumbnailImageUrl,
        List<String> imageUrls,
        OffsetDateTime createdAt
) {

    public static ParcelDetailedResponse of(Parcel parcel, UserProfileSummary user, String thumbnailImageUrl, List<String> imageUrls) {
        return ParcelDetailedResponse.builder()
                .id(parcel.getId())
                .user(user)
                .description(parcel.getDescription())
                .weightKg(parcel.getWeightKg())
                .size(parcel.getSize())
                .fragile(parcel.isFragile())
                .requireCode(parcel.getCodeOTP() != null)
                .pickupAddress(parcel.getPickupAddress())
                .dropoffAddress(parcel.getDropoffAddress())
                .status(parcel.getStatus())
                .thumbnailImageUrl(thumbnailImageUrl)
                .imageUrls(imageUrls)
                .createdAt(parcel.getCreatedAt())
                .build();
    }
}
