package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.trips.TripState;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;


@Builder
public record TripSummary(
        UUID id,
        Address departureAddress,
        Address arrivalAddress,
        LocalDate departureDate,
        LocalDate arrivalDate,
        BigDecimal availableWeightKg,
        BigDecimal pricePerKg,
        boolean instantBooking,
        TripState status,
        int stopCount,
        OffsetDateTime publishedAt
) {}
