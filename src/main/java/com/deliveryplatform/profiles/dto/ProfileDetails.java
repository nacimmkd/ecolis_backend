package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.images.dto.ImageDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProfileDetails(
        UUID profileId,
        String firstName,
        String lastName,
        String phone,
        BigDecimal avgRating,
        long totalReviews,
        int totalDeliveries,
        int totalOrders,
        ImageDto avatar
) {}