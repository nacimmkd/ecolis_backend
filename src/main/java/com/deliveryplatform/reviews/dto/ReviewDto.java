package com.deliveryplatform.reviews.dto;

import com.deliveryplatform.profiles.dto.ProfileBrief;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ReviewDto(
        UUID id,
        ProfileBrief reviewer,
        Short rating,
        String comment,
        OffsetDateTime createdAt
) {}
