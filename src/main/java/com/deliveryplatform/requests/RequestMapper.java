package com.deliveryplatform.requests;

import com.deliveryplatform.parcels.ParcelMapper;
import com.deliveryplatform.requests.dto.RequestDto;
import com.deliveryplatform.trips.TripMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {TripMapper.class, ParcelMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RequestMapper {

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "trip", source = "trip")
    @Mapping(target = "parcel", source = "parcel")
    @Mapping(target = "pickupDetour", source = "pickupDetourKm")
    @Mapping(target = "dropOffDetour", source = "dropOffDetourKm")
    RequestDto toRequestDto(Request request);

    List<RequestDto> toRequestDto(List<Request> requests);
}