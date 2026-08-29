package com.deliveryplatform.profiles.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProfileBrief(
        UUID userId,
        String firstName,
        String lastName,
        BigDecimal avgRating,
        int reviewCount,
        String avatarUrl,
        boolean verified
) {
}
