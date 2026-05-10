package com.deliveryplatform.parcels;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.parcels.dto.ParcelCreateRequest;
import com.deliveryplatform.parcels.dto.ParcelDetails;
import com.deliveryplatform.parcels.dto.ParcelSummary;
import com.deliveryplatform.users.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ParcelMapper {

    private final UserMapper userMapper;
    private final ImageService imageService;

    public ParcelSummary toSummaryDto(Parcel parcel) {
        return ParcelSummary.builder()
                .parcelId(parcel.getId())
                .owner(userMapper.toSummaryDto(parcel.getOwner()))
                .weightKg(parcel.getWeightKg())
                .size(parcel.getSize())
                .fragile(parcel.isFragile())
                .pickupAddress(parcel.getPickupAddress())
                .dropoffAddress(parcel.getDropoffAddress())
                .status(parcel.getStatus())
                .thumbnailImageUrl(resolveImageUrl(parcel.getThumbnailImage()))
                .createdAt(parcel.getCreatedAt())
                .build();
    }

    public ParcelDetails toDetailedDto(Parcel parcel) {
        return ParcelDetails.builder()
                .parcelId(parcel.getId())
                .owner(userMapper.toSummaryDto(parcel.getOwner()))
                .description(parcel.getDescription())
                .weightKg(parcel.getWeightKg())
                .size(parcel.getSize())
                .fragile(parcel.isFragile())
                .pickupAddress(parcel.getPickupAddress())
                .dropoffAddress(parcel.getDropoffAddress())
                .status(parcel.getStatus())
                .thumbnailImageUrl(resolveImageUrl(parcel.getThumbnailImage()))
                .imageUrls(parcel.getImages().stream()
                        .map(this::resolveImageUrl)
                        .toList())
                .createdAt(parcel.getCreatedAt())
                .build();
    }

    public Parcel toEntity(ParcelCreateRequest request) {
        return Parcel.builder()
                .description(request.description())
                .weightKg(request.weightKg())
                .size(request.size())
                .fragile(request.fragile())
                .build();
    }

    private String resolveImageUrl(Image image) {
        if (image == null || image.getId() == null) return null;
        return imageService.getReadUrl(image.getId());
    }
}