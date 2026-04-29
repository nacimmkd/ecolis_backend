package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TripStopRequest(
        @NotNull @Min(1) Integer stopOrder,
        @Valid @NotNull Address address
) {}
