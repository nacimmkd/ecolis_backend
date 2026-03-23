package com.deliveryplatform.parcels;

import com.deliveryplatform.common.addresses.Address;
import com.deliveryplatform.common.addresses.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ParcelDto {

    public record ParcelRequest(

            @Size(max = 500)
            String description,

            @NotNull @DecimalMin("0.01")
            BigDecimal weightKg,

            @NotNull ParcelSize size,

            @NotNull @DecimalMin("0.0") BigDecimal price,

            boolean fragile,

            @NotNull @Valid
            AddressRequest pickupAddress,

            @NotNull @Valid
            AddressRequest dropoffAddress,

            @Future
            LocalDate deadlineDate
    ) {}


    public record ParcelResponse(
            UUID            id,
            String          description,
            BigDecimal      weightKg,
            ParcelSize      size,
            BigDecimal      price,
            boolean         fragile,
            Address         pickupAddress,
            Address         dropoffAddress,
            ParcelStatus    status,
            LocalDate       deadlineDate,
            OffsetDateTime  createdAt
    ) {}


    public record ParcelStatusUpdate(
            @NotNull ParcelStatus status
    ) {}
}