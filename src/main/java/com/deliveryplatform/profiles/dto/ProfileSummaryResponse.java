package com.deliveryplatform.profiles.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record ProfileSummaryResponse(
        UUID profileId,
        String firstName,
        String lastName,
        BigDecimal avgRating,
        String avatarUrl
) {

    public ProfileSummaryResponse withAvatarUrl(String avatarUrl) {
        return new ProfileSummaryResponse(profileId, firstName, lastName,
                avgRating, avatarUrl);
    }
}