package com.deliveryplatform.trips;

import com.deliveryplatform.trips.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class TripMapper {

    public TripSummary toTripSummaryDto(Trip trip) {
        if (trip == null) {
            return null;
        }
        return TripSummary.builder()
                .tripId(trip.getId())
                .departureCity(trip.getDepartureAddress().toBriefAddress())
                .arrivalCity(trip.getArrivalAddress().toBriefAddress())
                .departureDate(trip.getDepartureDate())
                .arrivalDate(trip.getArrivalDate())
                .availableWeightKg(trip.getAvailableWeightKg())
                .remainingWeightKg(trip.getRemainingWeightKg())
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
                .tripId(trip.getId())
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

    public TripStopDto toTripStopDto(TripStop stop) {
        if (stop == null) {
            return null;
        }
        return TripStopDto.builder()
                .id(stop.getId())
                .order(stop.getOrder())
                .address(stop.getAddress())
                .build();
    }

    public List<TripStopDto> toTripStopDto(List<TripStop> stops) {
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
                .remainingWeightKg(request.availableWeightKg())
                .pricePerKg(request.pricePerKg())
                .instantBooking(request.instantBooking())
                .maxDetourKm(request.maxDetourKm())
                .notes(request.notes())
                .build();
    }
}