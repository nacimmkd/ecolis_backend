package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.payments.Price;
import com.deliveryplatform.trips.TripState;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Builder
public record TripDetails(
        UUID tripId,
        Address departureAddress,
        Address arrivalAddress,
        LocalDate departureDate,
        LocalDate arrivalDate,
        BigDecimal availableWeightKg,
        BigDecimal remainingWeightKg,
        Price pricePerKg,
        boolean instantBooking,
        BigDecimal maxDetourKm,
        TripState state,
        String notes,
        List<TripStopDto> stops,
        long bookingsCount,
        OffsetDateTime publishedAt
) {}