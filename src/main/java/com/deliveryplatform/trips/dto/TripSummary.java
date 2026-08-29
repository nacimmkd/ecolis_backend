package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.payments.Price;
import com.deliveryplatform.trips.TripState;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;


@Builder
public record TripSummary(
        UUID tripId,
        Address departure,
        Address arrival,
        LocalDate departureDate,
        LocalDate arrivalDate,
        BigDecimal availableWeightKg,
        BigDecimal remainingWeightKg,
        Price pricePerKg,
        boolean instantBooking,
        TripState state,
        int stopCount,
        OffsetDateTime publishedAt
) {}
