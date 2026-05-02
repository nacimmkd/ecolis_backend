package com.deliveryplatform.profiles.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfileResponse(
        UUID profileId,
        String firstName,
        String lastName,
        String phone,
        BigDecimal avgRating,
        int totalDeliveries,
        int totalOrders,
        String avatarUrl
) {

    public ProfileResponse withAvatarUrl(String avatarUrl) {
        return new ProfileResponse(profileId, firstName, lastName, phone,
                avgRating, totalDeliveries, totalOrders, avatarUrl);
    }

}