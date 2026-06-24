package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.AddressMapper;
import com.deliveryplatform.trips.dto.*;
import com.deliveryplatform.users.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class TripMapper {

    private final UserMapper userMapper;
    private final AddressMapper addressMapper;

    public TripSummary toTripSummaryDto(Trip trip) {
        if (trip == null) {
            return null;
        }
        return TripSummary.builder()
                .id(trip.getId())
                .departureAddress(trip.getDepartureAddress())
                .arrivalAddress(trip.getArrivalAddress())
                .departureDate(trip.getDepartureDate())
                .arrivalDate(trip.getArrivalDate())
                .availableWeightKg(trip.getAvailableWeightKg())
                .pricePerKg(trip.getPricePerKg())
                .instantBooking(trip.isInstantBooking())
                .status(trip.getState())
                .stopCount(trip.getStops().size())
                .publishedAt(trip.getCreatedAt())
                .build();
    }

    public TripDetails toTripDetailsDto(Trip trip) {
        if (trip == null) {
            return null;
        }
        return TripDetails.builder()
                .id(trip.getId())
                .owner(userMapper.toRefDto(trip.getOwner()))
                .departureAddress(trip.getDepartureAddress())
                .arrivalAddress(trip.getArrivalAddress())
                .departureDate(trip.getDepartureDate())
                .arrivalDate(trip.getArrivalDate())
                .availableWeightKg(trip.getAvailableWeightKg())
                .remainingWeightKg(trip.getRemainingWeightKg())
                .pricePerKg(trip.getPricePerKg())
                .instantBooking(trip.isInstantBooking())
                .maxDetourKm(trip.getMaxDetourKm())
                .status(trip.getState())
                .notes(trip.getNotes())
                .stops(trip.getStops().stream().map(this::toTripStopDto).toList())
                .publishedAt(trip.getCreatedAt())
                .build();
    }

    public StopPointResponse toTripStopDto(TripStop stop) {
        if (stop == null) {
            return null;
        }
        return StopPointResponse.builder()
                .id(stop.getId())
                .order(stop.getOrder())
                .address(stop.getAddress())
                .build();
    }

    public List<StopPointResponse> toTripStopDto(List<TripStop> stops) {
        if (stops == null) return List.of();
        return stops.stream().map(this::toTripStopDto).toList();
    }

    public Trip toTripEntity(TripCreateRequest request) {
        if (request == null) {
            return null;
        }
        return Trip.builder()
                .departureDate(request.departureDate())
                .arrivalDate(request.arrivalDate())
                .availableWeightKg(request.availableWeightKg())
                .pricePerKg(request.pricePerKg())
                .instantBooking(request.instantBooking())
                .maxDetourKm(request.maxDetourKm())
                .notes(request.notes())
                .build();
    }

    public TripStop toTripStopEntity(StopPointRequest request) {
        if (request == null) return null;
        return TripStop.builder()
                .order(request.order())
                .address(addressMapper.toEntity(request.address()))
                .build();
    }

    public List<TripStop> toTripStopEntity(List<StopPointRequest> stopPointRequests) {
        if (stopPointRequests == null) return List.of();
        return stopPointRequests.stream()
                .map(this::toTripStopEntity)
                .toList();
    }
}