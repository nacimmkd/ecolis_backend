package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.trips.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TripMapper {

    @Mapping(target = "tripId", source = "id")
    @Mapping(target = "departureCity", source = "departureAddress", qualifiedByName = "toBriefAddress")
    @Mapping(target = "arrivalCity", source = "arrivalAddress", qualifiedByName = "toBriefAddress")
    @Mapping(target = "stopCount", expression = "java(trip.getStops() == null ? 0 : trip.getStops().size())")
    @Mapping(target = "publishedAt", source = "createdAt")
    TripSummary toTripSummaryDto(Trip trip);

    @Mapping(target = "tripId", source = "id")
    @Mapping(target = "stops", source = "stops")
    @Mapping(target = "publishedAt", source = "createdAt")
    TripDetails toTripDetailsDto(Trip trip);

    TripStopDto toTripStopDto(TripStop stop);

    List<TripStopDto> toTripStopDto(List<TripStop> stops);

    @Named("toBriefAddress")
    default String toBriefAddress(Address address) {
        return address == null ? null : address.toBriefAddress();
    }
}