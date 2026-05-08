package com.deliveryplatform.trips;

import com.deliveryplatform.trips.dto.*;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@DecoratedWith(TripMapperDecorator.class)
public interface TripMapper {

    @Mapping(target = "tripId", source = "id")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "departureAddress", ignore = true)
    @Mapping(target = "arrivalAddress", ignore = true)
    @Mapping(target = "stops", ignore = true)
    TripSummary toSummaryDto(Trip trip);

    @Mapping(target = "tripId", source = "id")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "departureAddress", ignore = true)
    @Mapping(target = "arrivalAddress", ignore = true)
    @Mapping(target = "stops", ignore = true)
    TripDetails toDetailsDto(Trip trip);

    @Mapping(target = "tripStopId", source = "id")
    TripStopSummary toStopDto(TripStop stop);

    @Mapping(target = "id",                ignore = true)
    @Mapping(target = "owner",             ignore = true)
    @Mapping(target = "status",            ignore = true)
    @Mapping(target = "departureAddress",  ignore = true)
    @Mapping(target = "arrivalAddress",    ignore = true)
    @Mapping(target = "stops",             ignore = true)
    @Mapping(target = "bookings",          ignore = true)
    @Mapping(target = "deleted",           ignore = true)
    @Mapping(target = "deletedAt",         ignore = true)
    @Mapping(target = "createdAt",         ignore = true)
    @Mapping(target = "remainingWeightKg", ignore = true)
    Trip toEntity(TripCreateRequest request);
}