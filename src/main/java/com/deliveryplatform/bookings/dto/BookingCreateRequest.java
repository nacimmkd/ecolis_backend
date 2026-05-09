package com.deliveryplatform.bookings.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookingCreateRequest(
        @NotNull UUID tripId,
        @NotNull UUID parcelId
) {}
