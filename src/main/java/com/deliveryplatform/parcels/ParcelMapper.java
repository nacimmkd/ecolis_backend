package com.deliveryplatform.parcels;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.parcels.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ParcelImageMapper.class, ParcelBookingMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ParcelMapper {

    @Mapping(target = "parcelId", source = "id")
    @Mapping(target = "pickup", source = "pickupAddress")
    @Mapping(target = "dropoff", source = "dropoffAddress")
    @Mapping(target = "publishedAt", source = "createdAt")
    ParcelSummary toSummaryDto(Parcel parcel);

    List<ParcelSummary> toSummaryDto(List<Parcel> parcels);

    @Mapping(target = "parcel", source = "parcel")
    @Mapping(target = "bookings", source = "bookings")
    ParcelDetails toDetailedDto(Parcel parcel, List<Booking> bookings);

    @Mapping(target = "note", source = "message")
    TrackEventDto toTrackingEventDto(TrackEvent trackEvent);

    List<TrackEventDto> toListTrackingEventDto(List<TrackEvent> trackEvents);
}