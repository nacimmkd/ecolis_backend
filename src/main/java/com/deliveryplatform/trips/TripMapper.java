package com.deliveryplatform.trips;

import com.deliveryplatform.trips.dto.TripRequest;
import com.deliveryplatform.trips.dto.TripResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(source = "user.id",target = "userId")
    TripResponse toResponse(Trip trip);
    Trip toEntity(TripRequest request);
    void updateEntity(@MappingTarget Trip trip, TripRequest request);
}
