package com.deliveryplatform.trips;

import com.deliveryplatform.profiles.ProfileMapper;
import com.deliveryplatform.trips.dto.*;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = ProfileMapper.class)
public interface TripMapper {

    @Mapping(target = "tripId", source = "id")
    TripSummary toSummaryDto(Trip trip);

    @Mapping(target = "tripId", source = "id")
    TripDetails toDetailsDto(Trip trip);

    @Mapping(target = "tripStopId", source = "id")
    TripStopSummary toStopDto(TripStop stop);

    @Mapping(target = "id",                ignore = true)
    @Mapping(target = "owner",             ignore = true)
    @Mapping(target = "status",            ignore = true)
    @Mapping(target = "stops",             ignore = true)
    @Mapping(target = "bookings",          ignore = true)
    @Mapping(target = "deleted",           ignore = true)
    @Mapping(target = "deletedAt",         ignore = true)
    @Mapping(target = "createdAt",         ignore = true)
    @Mapping(target = "remainingWeightKg", ignore = true)
    Trip toEntity(TripCreateRequest request);
}