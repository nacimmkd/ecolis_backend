package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TripUpdateRequest(
        @Valid Address departureAddress,
        @Valid Address arrivalAddress,

        @Future LocalDate departureDate,
        @Future LocalDate arrivalDate,
        @DecimalMin("10.0") BigDecimal availableVolumeCm3,
        @DecimalMin("0.1") BigDecimal availableWeightKg,
        @DecimalMin("0.1") BigDecimal pricePerKg,
        boolean instantBooking,
        @DecimalMin("0.5") BigDecimal maxDetourKm,

        String notes,

        @Valid List<TripStopRequest> stops
) {}

