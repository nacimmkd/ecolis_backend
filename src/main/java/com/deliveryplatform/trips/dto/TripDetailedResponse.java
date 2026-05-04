package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import com.deliveryplatform.trips.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


public record TripDetailedResponse(
        UUID tripId,
        ProfileSummary owner,
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

    public TripDetailedResponse withOwner(ProfileSummary owner) {
        return new TripDetailedResponse(tripId, owner, departureAddress, arrivalAddress,
                departureDate, arrivalDate, availableVolumeCm3, availableWeightKg,
                pricePerKg, instantBooking, status, notes, createdAt, stops);
    }

    public TripDetailedResponse withDepartureAddress(GeocodedAddress departureAddress) {
        return new TripDetailedResponse(tripId, owner, departureAddress, arrivalAddress,
                departureDate, arrivalDate, availableVolumeCm3, availableWeightKg,
                pricePerKg, instantBooking, status, notes, createdAt, stops);
    }

    public TripDetailedResponse withArrivalAddress(GeocodedAddress arrivalAddress) {
        return new TripDetailedResponse(tripId, owner, departureAddress, arrivalAddress,
                departureDate, arrivalDate, availableVolumeCm3, availableWeightKg,
                pricePerKg, instantBooking, status, notes, createdAt, stops);
    }

    public TripDetailedResponse withStops(List<TripStopResponse> stops) {
        return new TripDetailedResponse(tripId, owner, departureAddress, arrivalAddress,
                departureDate, arrivalDate, availableVolumeCm3, availableWeightKg,
                pricePerKg, instantBooking, status, notes, createdAt, stops);
    }
}