package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.profiles.dto.ProfileSummaryResponse;
import com.deliveryplatform.trips.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TripSummaryResponse(
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
        OffsetDateTime createdAt
) {

    public TripSummaryResponse withOwner(ProfileSummaryResponse owner) {
        return new TripSummaryResponse(tripId, owner, departureAddress, arrivalAddress,
                departureDate, arrivalDate, availableVolumeCm3, availableWeightKg,
                pricePerKg, instantBooking, status, createdAt);
    }

    public TripSummaryResponse withDepartureAddress(GeocodedAddress departureAddress) {
        return new TripSummaryResponse(tripId, owner, departureAddress, arrivalAddress,
                departureDate, arrivalDate, availableVolumeCm3, availableWeightKg,
                pricePerKg, instantBooking, status, createdAt);
    }

    public TripSummaryResponse withArrivalAddress(GeocodedAddress arrivalAddress) {
        return new TripSummaryResponse(tripId, owner, departureAddress, arrivalAddress,
                departureDate, arrivalDate, availableVolumeCm3, availableWeightKg,
                pricePerKg, instantBooking, status, createdAt);
    }
}
