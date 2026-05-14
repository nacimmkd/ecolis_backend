package com.deliveryplatform.reviews.dto;

import com.deliveryplatform.users.dto.UserSummary;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ReviewDto(
        UUID id,
        UserSummary reviewer,
        Short rating,
        String comment,
        OffsetDateTime createdAt
) {}
