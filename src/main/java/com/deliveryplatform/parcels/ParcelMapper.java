package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.parcels.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ImageMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ParcelMapper {

    @Mapping(target = "parcelId", source = "id")
    @Mapping(target = "fragile", source = "fragile")
    @Mapping(target = "pickupCity", source = "pickupAddress", qualifiedByName = "toBriefAddress")
    @Mapping(target = "dropoffCity", source = "dropoffAddress", qualifiedByName = "toBriefAddress")
    @Mapping(target = "publishedAt", source = "createdAt")
    ParcelSummary toSummaryDto(Parcel parcel);

    List<ParcelSummary> toSummaryDto(List<Parcel> parcels);

    @Mapping(target = "parcelId", source = "parcel.id")
    @Mapping(target = "fragile", source = "parcel.fragile")
    @Mapping(target = "publishedAt", source = "parcel.createdAt")
    @Mapping(target = "bookingsCount", source = "bookingsCount")
    ParcelDetails toDetailedDto(Parcel parcel, long bookingsCount);

    @Mapping(target = "note", source = "message")
    TrackEventDto toTrackingEventDto(TrackEvent trackEvent);

    List<TrackEventDto> toListTrackingEventDto(List<TrackEvent> trackEvents);

    @Named("toBriefAddress")
    default String toBriefAddress(Address address) {
        return address == null ? null : address.toBriefAddress();
    }
}