package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.parcels.ParcelSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ParcelUpdateRequest(
        @NotNull @Size(max = 500)
        String description,

        @NotNull @DecimalMin("0.01")
        BigDecimal weightKg,

        @NotNull ParcelSize size,

        @NotNull Boolean fragile,

        @Valid AddressRequest pickupAddress,

        @Valid AddressRequest dropoffAddress,

        @NotNull List<UUID> imageIds,

        UUID thumbnailId
) {
}
