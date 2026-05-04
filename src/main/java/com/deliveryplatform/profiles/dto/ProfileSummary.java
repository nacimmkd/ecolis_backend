package com.deliveryplatform.profiles.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record UserSummary(
        UUID profileId,
        String firstName,
        String lastName,
        BigDecimal avgRating,
        String avatarUrl
) {

    public UserSummary withAvatarUrl(String avatarUrl) {
        return new UserSummary(profileId, firstName, lastName,
                avgRating, avatarUrl);
    }
}