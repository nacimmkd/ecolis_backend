package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.trips.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TripMapper {

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "departureCity", source = "trip.departureAddress", qualifiedByName = "toBriefAddress")
    @Mapping(target = "arrivalCity", source = "trip.arrivalAddress", qualifiedByName = "toBriefAddress")
    @Mapping(target = "stopCount", expression = "java(trip.getStops() == null ? 0 : trip.getStops().size())")
    @Mapping(target = "publishedAt", source = "createdAt")
    TripSummary toTripSummaryDto(Trip trip);

    List<TripSummary> toTripSummaryDto(List<Trip> trips);

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "stops", source = "trip.stops")
    @Mapping(target = "publishedAt", source = "trip.createdAt")
    @Mapping(target = "requestsCount", source = "requestsCount")
    @Mapping(target = "bookingsCount", source = "bookingsCount")
    TripDetails toTripDetailsDto(Trip trip, long requestsCount, long bookingsCount);

    TripStopDto toTripStopDto(TripStop stop);

    List<TripStopDto> toTripStopDto(List<TripStop> stops);

    @Named("toBriefAddress")
    default String toBriefAddress(Address address) {
        return address == null ? null : address.toBriefAddress();
    }
}