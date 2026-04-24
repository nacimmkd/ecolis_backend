package com.deliveryplatform.parcels.dto;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.parcels.ParcelSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelRequest(

        @Size(max = 500)
        String description,

        @NotNull @DecimalMin("0.01")
        BigDecimal weightKg,

        @NotNull ParcelSize size,

        boolean fragile,

        Boolean requireCode,

        @NotNull @Valid
        Address pickupAddress,

        @NotNull @Valid
        Address dropoffAddress,

        @Future
        LocalDate deadlineDate
) {
}
