package com.deliveryplatform.requests;

import com.deliveryplatform.parcels.ParcelMapper;
import com.deliveryplatform.requests.dto.ParcelRequestDto;
import com.deliveryplatform.requests.dto.RequestDto;
import com.deliveryplatform.requests.dto.TripRequestDto;
import com.deliveryplatform.trips.TripMapper;
import com.deliveryplatform.users.UserBriefMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {TripMapper.class, ParcelMapper.class, UserBriefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RequestMapper {

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "trip", source = "trip")
    @Mapping(target = "parcel", source = "parcel")
    @Mapping(target = "pickupDetourKm", source = "pickupDetourKm")
    @Mapping(target = "dropOffDetourKm", source = "dropOffDetourKm")
    RequestDto toRequestDto(Request request);

    List<RequestDto> toRequestDto(List<Request> requests);

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "parcel", source = "parcel")
    @Mapping(target = "sender", source = "parcel.owner")
    @Mapping(target = "pickupDetourKm", source = "pickupDetourKm")
    @Mapping(target = "dropOffDetourKm", source = "dropOffDetourKm")
    TripRequestDto toTripRequestDto(Request request);

    List<TripRequestDto> toTripRequestDto(List<Request> requests);

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "trip", source = "trip")
    @Mapping(target = "carrier", source = "trip.owner")
    @Mapping(target = "pickupDetourKm", source = "pickupDetourKm")
    @Mapping(target = "dropOffDetourKm", source = "dropOffDetourKm")
    ParcelRequestDto toParcelRequestDto(Request request);

    List<ParcelRequestDto> toParcelRequestDto(List<Request> requests);


}