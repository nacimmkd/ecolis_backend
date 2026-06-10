package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TripCreateRequest(
        @Valid @NotNull AddressRequest departureAddress,
        @Valid @NotNull AddressRequest arrivalAddress,

        @NotNull @Future LocalDate departureDate,
        @NotNull @Future LocalDate arrivalDate,
        @DecimalMin("0.1") BigDecimal availableWeightKg,
        @NotNull @DecimalMin("0.1") BigDecimal pricePerKg,
        boolean instantBooking,
        @DecimalMin("0.5") BigDecimal maxDetourKm,

        String notes,

        @Valid List<StopPointRequest> stops
) {}
