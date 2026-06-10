package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.trips.TripStatus;
import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Builder
public record TripDetails(
        UUID tripId,
        UserBrief owner,
        Address departureAddress,
        Address arrivalAddress,
        LocalDate departureDate,
        LocalDate arrivalDate,
        BigDecimal availableWeightKg,
        BigDecimal remainingWeightKg,
        BigDecimal pricePerKg,
        boolean instantBooking,
        BigDecimal maxDetourKm,
        TripStatus status,
        String notes,
        List<StopPoint> stops,
        OffsetDateTime publishedAt
) {}