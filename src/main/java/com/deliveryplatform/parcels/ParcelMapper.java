package com.deliveryplatform.parcels;


import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.parcels.dto.*;
import com.deliveryplatform.users.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ParcelMapper {

    private final UserMapper userMapper;
    private final ImageMapper imageMapper;

    public ParcelSummaryDto toSummaryDto(Parcel parcel) {
        return ParcelSummaryDto.builder()
                .parcelId(parcel.getId())
                .weightKg(parcel.getWeightKg())
                .size(parcel.getSize())
                .fragile(parcel.isFragile())
                .pickupCity(parcel.getPickupAddress().toBriefAddress())
                .dropoffCity(parcel.getDropoffAddress().toBriefAddress())
                .state(parcel.getState())
                .thumbnail(imageMapper.toDto(parcel.getThumbnail()))
                .publishedAt(parcel.getCreatedAt())
                .build();
    }

    public ParcelOwnerDto toDetailedDto(Parcel parcel) {
        return ParcelOwnerDto.builder()
                .parcelId(parcel.getId())
                .description(parcel.getDescription())
                .weightKg(parcel.getWeightKg())
                .size(parcel.getSize())
                .fragile(parcel.isFragile())
                .pickupAddress(parcel.getPickupAddress())
                .dropoffAddress(parcel.getDropoffAddress())
                .state(parcel.getState())
                .thumbnail(imageMapper.toDto(parcel.getThumbnail()))
                .images(imageMapper.toDto(parcel.getImages()))
                .createdAt(parcel.getCreatedAt())
                .build();
    }

    public ParcelPublicDto toPublicDto(Parcel parcel) {
        return ParcelPublicDto.builder()
                .parcelId(parcel.getId())
                .owner(userMapper.toRefDto(parcel.getOwner()))
                .description(parcel.getDescription())
                .weightKg(parcel.getWeightKg())
                .size(parcel.getSize())
                .fragile(parcel.isFragile())
                .pickupAddress(parcel.getPickupAddress())
                .dropoffAddress(parcel.getDropoffAddress())
                .thumbnail(imageMapper.toDto(parcel.getThumbnail()))
                .images(imageMapper.toDto(parcel.getImages()))
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

    public TrackEventDto toTrackingEventDto(TrackEvent trackEvent) {
        return TrackEventDto.builder()
                .id(trackEvent.getId())
                .status(trackEvent.getState())
                .note(trackEvent.getMessage())
                .occurredAt(trackEvent.getOccurredAt())
                .build();
    }

    public List<TrackEventDto> toListTrackingEventDto(List<TrackEvent> trackEvents) {
        return trackEvents.stream().map(this::toTrackingEventDto).toList();
    }
}