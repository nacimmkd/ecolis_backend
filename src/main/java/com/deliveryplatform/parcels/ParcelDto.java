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

            @DecimalMin("0.0") BigDecimal lengthCm,
            @DecimalMin("0.0") BigDecimal widthCm,
            @DecimalMin("0.0") BigDecimal heightCm,

            boolean isFragile,

            @NotNull @Valid
            AddressRequest pickupAddress,

            @NotNull @Valid
            AddressRequest dropoffAddress,

            @Future
            LocalDate deadlineDate
    ) {}


    public record ParcelResponse(
            UUID            id,
            UUID            userId,
            String          description,
            BigDecimal      weightKg,
            BigDecimal      lengthCm,
            BigDecimal      widthCm,
            BigDecimal      heightCm,
            boolean         isFragile,
            Address pickupAddress,
            Address dropoffAddress,
            ParcelStatus    status,
            LocalDate       deadlineDate,
            OffsetDateTime  createdAt
    ) {}


    public record ParcelStatusUpdate(
            @NotNull ParcelStatus status
    ) {}
}