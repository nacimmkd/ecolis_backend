package com.deliveryplatform.parcels;


import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.parcels.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ParcelMapper {

    private final ImageMapper imageMapper;

    public ParcelSummary toSummaryDto(Parcel parcel) {
        return ParcelSummary.builder()
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

    public ParcelDetails toDetailedDto(Parcel parcel) {
        return ParcelDetails.builder()
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