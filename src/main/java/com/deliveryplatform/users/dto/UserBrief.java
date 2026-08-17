package com.deliveryplatform.users.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record UserBrief(
        UUID userId,
        String firstName,
        String lastName,
        BigDecimal avgRating,
        int reviewCount,
        String avatarUrl,
        boolean verified
) {
}
