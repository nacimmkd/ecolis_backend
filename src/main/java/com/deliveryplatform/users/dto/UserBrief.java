package com.deliveryplatform.users.dto;

import com.deliveryplatform.images.dto.ImageDto;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserBrief(
        UUID userId,
        String firstName,
        String lastName,
        Double avgRating,
        ImageDto avatar,
        boolean verified
) {
}
