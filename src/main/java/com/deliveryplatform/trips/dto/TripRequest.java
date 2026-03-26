package com.deliveryplatform.trips.dto;

import com.deliveryplatform.common.addresses.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TripRequest(
        @Valid @NotNull Address departure,
        @Valid @NotNull Address arrival,

        @NotNull @FutureOrPresent LocalDate departureDate,
        @NotNull @FutureOrPresent LocalDate arrivalDate,
        @DecimalMin("0.0") BigDecimal availableVolumeCm3,
        @DecimalMin("0.0") BigDecimal availableWeightKg,
        @NotNull @DecimalMin("0.0") BigDecimal pricePerKg,
        boolean instantBooking,
        @DecimalMin("0.0") BigDecimal maxDetourKm,

        String notes,

        @Valid List<StopRequest> stops
) {}
