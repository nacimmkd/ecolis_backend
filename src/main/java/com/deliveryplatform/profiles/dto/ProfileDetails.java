package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.reviews.dto.ReviewDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
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
        int deliveredParcels,
        List<ReviewDto> reviews,
        String avatarUrl
) {}