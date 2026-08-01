package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.payments.Price;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TripCreateRequest(

        @Valid @NotNull
        AddressRequest departureAddress,

        @Valid @NotNull
        AddressRequest arrivalAddress,

        @NotNull @Future
        LocalDate departureDate,

        @NotNull @Future
        LocalDate arrivalDate,

        @DecimalMin("1")
        BigDecimal availableWeightKg,

        @Valid @NotNull
        Price pricePerKg,

        @NotNull boolean instantBooking,

        @NotNull @DecimalMin("1") BigDecimal maxDetourKm,

        String notes
) {}
