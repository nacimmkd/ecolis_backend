package com.deliveryplatform.trips.dto;

import com.deliveryplatform.common.addresses.GeoAddress;
import com.deliveryplatform.trips.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TripResponse(
        UUID id,
        UUID userId,
        GeoAddress departure,
        GeoAddress arrival,
        LocalDate departureDate,
        LocalDate arrivalDate,
        BigDecimal availableVolumeCm3,
        BigDecimal availableWeightKg,
        BigDecimal pricePerKg,
        boolean instantBooking,
        TripStatus status,
        String notes,
        List<StopResponse> stops
) {}
