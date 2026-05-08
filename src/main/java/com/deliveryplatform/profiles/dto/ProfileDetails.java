package com.deliveryplatform.profiles.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder(toBuilder = true)
public record ProfileDetails(
        UUID profileId,
        String firstName,
        String lastName,
        String phone,
        BigDecimal avgRating,
        int totalDeliveries,
        int totalOrders,
        String avatarUrl
) {}