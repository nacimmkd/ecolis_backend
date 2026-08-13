package com.deliveryplatform.parcels;

import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.parcels.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ImageMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ParcelMapper {

    @Mapping(target = "parcelId", source = "id")
    @Mapping(target = "pickup", source = "pickupAddress")
    @Mapping(target = "dropoff", source = "dropoffAddress")
    @Mapping(target = "publishedAt", source = "createdAt")
    ParcelSummary toSummaryDto(Parcel parcel);

    List<ParcelSummary> toSummaryDto(List<Parcel> parcels);

    @Mapping(target = "parcelId", source = "parcel.id")
    @Mapping(target = "fragile", source = "parcel.fragile")
    @Mapping(target = "publishedAt", source = "parcel.createdAt")
    ParcelDetails toDetailedDto(Parcel parcel);

    @Mapping(target = "note", source = "message")
    TrackEventDto toTrackingEventDto(TrackEvent trackEvent);

    List<TrackEventDto> toListTrackingEventDto(List<TrackEvent> trackEvents);
}