package com.deliveryplatform.trips;

import com.deliveryplatform.trips.TripStopDto.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TripStopMapper {
    StopResponse toResponse(TripStop tripStop);
    List<TripStop> toEntityList(List<StopRequest> request);
}
