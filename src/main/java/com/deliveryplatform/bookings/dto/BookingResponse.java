package com.deliveryplatform.bookings.dto;

import com.deliveryplatform.bookings.BookingStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID tripId,
        UUID parcelId,
        UUID senderId,
        UUID carrierId,
        BookingStatus status,
        BigDecimal price,
        OffsetDateTime acceptedAt,
        OffsetDateTime completedAt,
        OffsetDateTime createdAt
) {}
