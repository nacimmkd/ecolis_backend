package com.deliveryplatform.trips.dto;

import com.deliveryplatform.addresses.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StopPointRequest(
        @NotNull @Min(1) Integer stopOrder,
        @Valid @NotNull AddressRequest address
) {}
