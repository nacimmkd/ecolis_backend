package com.deliveryplatform.trips;

import com.deliveryplatform.trips.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(target = "tripId", source = "id")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "departureAddress", ignore = true)
    @Mapping(target = "arrivalAddress", ignore = true)
    @Mapping(target = "stops", ignore = true)
    TripDetailedResponse toDetailedResponse(Trip trip);

    @Mapping(target = "tripId", source = "id")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "departureAddress", ignore = true)
    @Mapping(target = "arrivalAddress", ignore = true)
    TripSummaryResponse toSummaryResponse(Trip trip);

    @Mapping(target = "tripStopId", source = "id")
    @Mapping(target = "address", ignore = true)
    TripStopResponse toStopResponse(TripStop tripStop);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "departureAddress", ignore = true)
    @Mapping(target = "arrivalAddress", ignore = true)
    @Mapping(target = "stops", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Trip toEntity(TripCreateRequest request);
}