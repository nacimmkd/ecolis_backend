package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.images.dto.ImageDto;
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
        BigDecimal avgRating,
        int reviewCount,
        int completedTrips,
        int deliveredParcels,
        List<ReviewDto> reviews,
        ImageDto avatar
) {}