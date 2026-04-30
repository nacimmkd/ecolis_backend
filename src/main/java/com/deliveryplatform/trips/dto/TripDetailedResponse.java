package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.profiles.dto.ProfileSummaryResponse;
import com.deliveryplatform.trips.Trip;
import com.deliveryplatform.trips.TripStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record TripDetailedResponse(
        UUID tripId,
        ProfileSummaryResponse owner,
        GeocodedAddress departureAddress,
        GeocodedAddress arrivalAddress,
        LocalDate departureDate,
        LocalDate arrivalDate,
        BigDecimal availableVolumeCm3,
        BigDecimal availableWeightKg,
        BigDecimal pricePerKg,
        boolean instantBooking,
        TripStatus status,
        String notes,
        OffsetDateTime createdAt,
        List<TripStopResponse> stops
) {

    public static TripDetailedResponse of(Trip trip, ProfileSummaryResponse owner, List<TripStopResponse> stops) {
        return TripDetailedResponse.builder()
                .tripId(trip.getId())
                .owner(owner)
                .departureAddress(trip.getDepartureAddress())
                .arrivalAddress(trip.getArrivalAddress())
                .departureDate(trip.getDepartureDate())
                .arrivalDate(trip.getArrivalDate())
                .availableVolumeCm3(trip.getAvailableVolumeCm3())
                .availableWeightKg(trip.getAvailableWeightKg())
                .pricePerKg(trip.getPricePerKg())
                .instantBooking(trip.isInstantBooking())
                .status(trip.getStatus())
                .notes(trip.getNotes())
                .stops(stops)
                .createdAt(trip.getCreatedAt())
                .build();
    }
}
