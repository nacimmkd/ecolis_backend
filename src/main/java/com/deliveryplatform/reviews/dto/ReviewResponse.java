package com.deliveryplatform.reviews.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID reviewerId,
        UUID revieweeId,
        UUID bookingId,
        Short rating,
        String comment,
        OffsetDateTime createdAt
) {
}
