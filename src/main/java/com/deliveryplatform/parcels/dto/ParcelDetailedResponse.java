package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.images.dto.ImageResponse;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelSize;
import com.deliveryplatform.parcels.ParcelStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ParcelDetailedResponse(
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
        LocalDate deadlineDate,
        String thumbnailImageUrl,
        List<String> imageUrls,
        OffsetDateTime createdAt
) {

    public static ParcelDetailedResponse of(Parcel parcel, String thumbnailImageUrl, List<String> imageUrls) {
        return ParcelDetailedResponse.builder()
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
                .imageUrls(imageUrls)
                .deadlineDate(parcel.getDeadlineDate())
                .createdAt(parcel.getCreatedAt())
                .build();
    }
}
