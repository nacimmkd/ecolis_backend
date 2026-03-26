package com.deliveryplatform.trips;

import com.deliveryplatform.trips.dto.StopRequest;
import com.deliveryplatform.trips.dto.StopResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TripStopMapper {
    StopResponse toResponse(TripStop tripStop);
    List<TripStop> toEntityList(List<StopRequest> request);
}
