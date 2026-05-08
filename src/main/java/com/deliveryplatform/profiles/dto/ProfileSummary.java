package com.deliveryplatform.profiles.dto;


import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder(toBuilder = true)
public record ProfileSummary(
        UUID profileId,
        String firstName,
        String lastName,
        BigDecimal avgRating,
        String avatarUrl
) {}