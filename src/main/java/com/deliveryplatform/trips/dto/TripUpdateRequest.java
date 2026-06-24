package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TripUpdateRequest(

        @Valid
        AddressRequest departureAddress,

        @Valid
        AddressRequest arrivalAddress,

        @NotNull @FutureOrPresent
        LocalDate departureDate,

        @NotNull @Future
        LocalDate arrivalDate,

        @NotNull @DecimalMin("10.0")
        BigDecimal availableVolumeCm3,

        @NotNull @DecimalMin("0.1")
        BigDecimal availableWeightKg,

        @NotNull @DecimalMin("0.1")
        BigDecimal pricePerKg,

        @NotNull boolean instantBooking,

        @NotNull @DecimalMin("0.5")
        BigDecimal maxDetourKm,

        @NotNull String notes,

        @Valid List<StopPointRequest> stops
) {}

