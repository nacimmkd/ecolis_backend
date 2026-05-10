package com.deliveryplatform.reviews.dto;

import com.deliveryplatform.users.dto.UserSummary;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ReviewDto(
        UUID id,
        UUID bookingId,
        UserSummary reviewer,
        UserSummary reviewee,
        Short rating,
        String comment,
        OffsetDateTime createdAt
) {}
