package com.deliveryplatform.profiles.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder(toBuilder = true)
public record ProfileDto(
        UUID profileId,
        String firstName,
        String lastName,
        String phone,
        boolean phoneVisible,
        String country,
        BigDecimal avgRating,
        int reviewCount,
        int completedTrips,
        int sentParcels,
        String avatarUrl
) {}