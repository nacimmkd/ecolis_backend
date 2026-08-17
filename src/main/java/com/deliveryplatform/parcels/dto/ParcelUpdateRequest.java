package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.parcels.ParcelSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ParcelUpdateRequest(

        String title,

        @NotNull @DecimalMin("0.01")
        BigDecimal weightKg,

        @NotNull ParcelSize size,

        @NotNull Boolean fragile,

        @Valid AddressRequest pickupAddress,

        @Valid AddressRequest dropoffAddress
) {
}
