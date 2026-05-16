package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.trips.TripStatus;
import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record TripSummary(
        UUID tripId,
        UserBrief owner,
        GeocodedAddress departureAddress,
        GeocodedAddress arrivalAddress,
        LocalDate departureDate,
        LocalDate arrivalDate,
        BigDecimal availableWeightKg,
        BigDecimal remainingWeightKg,
        BigDecimal pricePerKg,
        boolean instantBooking,
        TripStatus status,
        List<TripStopSummary> stops,
        OffsetDateTime createdAt
) {}
