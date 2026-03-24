package com.deliveryplatform.trips;

import com.deliveryplatform.trips.TripStopDto.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TripStopMapper {

    StopResponse toResponse(TripStop tripStop);
    TripStop toEntity(StopRequest request);
}
