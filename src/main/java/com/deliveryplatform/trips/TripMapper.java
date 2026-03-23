package com.deliveryplatform.trips;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.deliveryplatform.trips.TripDto.*;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(source = "user.id",target = "userId")
    TripResponse toResponse(Trip trip);
    Trip toEntity(TripRequest request);
    void updateEntity(@MappingTarget Trip trip, TripRequest request);
}
