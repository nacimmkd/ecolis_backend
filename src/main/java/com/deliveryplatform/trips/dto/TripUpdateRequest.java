package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.payments.Price;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TripUpdateRequest(

        @Valid
        AddressRequest departureAddress,

        @Valid
        AddressRequest arrivalAddress,

        @NotNull @FutureOrPresent
        LocalDate departureDate,

        @NotNull @Future
        LocalDate arrivalDate,

        @NotNull @DecimalMin("1")
        BigDecimal availableWeightKg,

        @Valid @NotNull
        Price pricePerKg,

        @NotNull boolean instantBooking,

        @NotNull @DecimalMin("1")
        BigDecimal maxDetourKm,

        @NotNull String notes
) {}

