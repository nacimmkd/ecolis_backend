package com.deliveryplatform.requests.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRequest(
        @NotNull UUID tripId,
        @NotNull UUID parcelId
) {}
