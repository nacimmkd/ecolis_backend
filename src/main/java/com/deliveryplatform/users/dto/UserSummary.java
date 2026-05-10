package com.deliveryplatform.users.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record UserSummary(
        UUID userId,
        String firstName,
        String lastName,
        BigDecimal avgRating,
        String avatarUrl,
        boolean verified
) {}