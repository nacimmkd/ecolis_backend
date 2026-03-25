package com.deliveryplatform.parcels;

import com.deliveryplatform.common.addresses.GeoAddress;
import com.deliveryplatform.common.addresses.Address;
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

            boolean fragile,

            Boolean requireCode,

            @NotNull @Valid
            Address pickupAddress,

            @NotNull @Valid
            Address dropoffAddress,

            @Future
            LocalDate deadlineDate
    ) {}


    public record ParcelResponse(
            UUID            id,
            UUID            userId,
            String          description,
            BigDecimal      weightKg,
            ParcelSize      size,
            boolean         fragile,
            GeoAddress      pickupAddress,
            GeoAddress      dropoffAddress,
            ParcelStatus    status,
            LocalDate       deadlineDate,
            OffsetDateTime  createdAt
    ) {}

    public record ParcelWithCodeResponse(
            UUID            id,
            UUID            userId,
            String          description,
            BigDecimal      weightKg,
            ParcelSize      size,
            boolean         fragile,
            String          codeOTP,
            GeoAddress      pickupAddress,
            GeoAddress      dropoffAddress,
            ParcelStatus    status,
            LocalDate       deadlineDate,
            OffsetDateTime  createdAt
    ) {}


    public record ParcelStatusUpdate(
            @NotNull ParcelStatus status
    ) {}
}