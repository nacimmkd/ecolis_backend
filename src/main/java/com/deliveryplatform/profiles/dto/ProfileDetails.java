package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.images.dto.ImageDto;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ProfileDetails(
        UUID profileId,
        String firstName,
        String lastName,
        String phone,
        Double avgRating,
        Long reviewCount,
        Long completedTrips,
        Long deliveredParcels,
        ImageDto avatar
) {}