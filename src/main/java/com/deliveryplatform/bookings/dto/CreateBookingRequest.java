package com.deliveryplatform.bookings.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID tripId,
        @NotNull UUID parcelId
) {}
