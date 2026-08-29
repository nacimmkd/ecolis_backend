package com.deliveryplatform.trips;

import com.deliveryplatform.payments.Price;
import com.deliveryplatform.trips.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TripMapper {

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "departure", source = "trip.departureAddress")
    @Mapping(target = "arrival", source = "trip.arrivalAddress")
    @Mapping(target = "stopCount", expression = "java(trip.getStops() == null ? 0 : trip.getStops().size())")
    @Mapping(target = "publishedAt", source = "createdAt")
    TripSummary toTripSummaryDto(Trip trip);

    List<TripSummary> toTripSummaryDto(List<Trip> trips);

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "stops", source = "trip.stops")
    @Mapping(target = "publishedAt", source = "trip.createdAt")
    @Mapping(target = "newRequestCount", source = "newRequestCount")
    @Mapping(target = "acceptedBookingsCount", source = "acceptedBookingsCount")
    @Mapping(target = "estimatedEarning", source = "estimatedEarning")
    TripDetails toTripDetailsDto(Trip trip, long newRequestCount, long acceptedBookingsCount, Price estimatedEarning);

    TripStopDto toTripStopDto(TripStop stop);

    List<TripStopDto> toTripStopDto(List<TripStop> stops);
}