package com.deliveryplatform.profiles.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProfileDetails(
        UUID profileId,
        String firstName,
        String lastName,
        String phone,
        String country,
        BigDecimal avgRating,
        int reviewCount,
        int completedTrips,
        int sentParcels,
        String avatarUrl
) {}