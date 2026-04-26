package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ParcelResponse(
        UUID id,
        UUID userId,
        String description,
        BigDecimal weightKg,
        ParcelSize size,
        boolean fragile,
        boolean requireCode,
        GeocodedAddress pickupAddress,
        GeocodedAddress dropoffAddress,
        ParcelStatus status,
        String thumbnailImageUrl,
        OffsetDateTime createdAt
) {

    public static ParcelResponse of(Parcel parcel, String thumbnailImageUrl) {
        return ParcelResponse.builder()
                .id(parcel.getId())
                .userId(parcel.getUser().getId())
                .description(parcel.getDescription())
                .weightKg(parcel.getWeightKg())
                .size(parcel.getSize())
                .fragile(parcel.isFragile())
                .requireCode(parcel.getCodeOTP() != null)
                .pickupAddress(parcel.getPickupAddress())
                .dropoffAddress(parcel.getDropoffAddress())
                .status(parcel.getStatus())
                .thumbnailImageUrl(thumbnailImageUrl)
                .createdAt(parcel.getCreatedAt())
                .build();
    }
}
