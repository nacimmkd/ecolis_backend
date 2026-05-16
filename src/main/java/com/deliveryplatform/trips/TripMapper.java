package com.deliveryplatform.trips;

import com.deliveryplatform.trips.dto.*;
import com.deliveryplatform.users.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TripMapper {

    private final UserMapper userMapper;

    public TripSummary toSummaryDto(Trip trip) {
        if (trip == null) {
            return null;
        }
        return TripSummary.builder()
                .tripId(trip.getId())
                .owner(userMapper.toRefDto(trip.getOwner()))
                .departureAddress(trip.getDepartureAddress())
                .arrivalAddress(trip.getArrivalAddress())
                .departureDate(trip.getDepartureDate())
                .arrivalDate(trip.getArrivalDate())
                .availableWeightKg(trip.getAvailableWeightKg())
                .remainingWeightKg(trip.getRemainingWeightKg())
                .pricePerKg(trip.getPricePerKg())
                .instantBooking(trip.isInstantBooking())
                .status(trip.getStatus())
                .stops(trip.getStops().stream().map(this::toSummaryDto).toList())
                .createdAt(trip.getCreatedAt())
                .build();
    }

    public TripDetails toDetailsDto(Trip trip) {
        if (trip == null) {
            return null;
        }
        return TripDetails.builder()
                .tripId(trip.getId())
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
                .status(trip.getStatus())
                .notes(trip.getNotes())
                .stops(trip.getStops().stream().map(this::toSummaryDto).toList())
                .createdAt(trip.getCreatedAt())
                .build();
    }

    public TripStopSummary toSummaryDto(TripStop stop) {
        if (stop == null) {
            return null;
        }
        return TripStopSummary.builder()
                .tripStopId(stop.getId())
                .stopOrder(stop.getStopOrder())
                .address(stop.getAddress())
                .build();
    }

    public Trip toEntity(TripCreateRequest request) {
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
}