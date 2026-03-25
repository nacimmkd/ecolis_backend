package com.deliveryplatform.bookings;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class BookingDto {

    public record CreateRequest(
            @NotNull UUID tripId,
            @NotNull UUID parcelId,
            String message
    ) {}

    public record UpdateStatusRequest(
            @NotNull BookingStatus status
    ) {}

    public record Response(
            UUID id,
            UUID tripId,
            UUID parcelId,
            UUID requesterId,
            BookingStatus status,
            BigDecimal price,
            String message,
            OffsetDateTime confirmedAt,
            OffsetDateTime pickedUpAt,
            OffsetDateTime deliveredAt,
            OffsetDateTime createdAt
    ) {}

}
