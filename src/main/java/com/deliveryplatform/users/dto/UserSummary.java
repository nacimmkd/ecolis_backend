package com.deliveryplatform.users.dto;

import com.deliveryplatform.images.dto.ImageDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record UserSummary(
        UUID userId,
        String firstName,
        String lastName,
        BigDecimal avgRating,
        ImageDto avatar,
        boolean verified
) {}