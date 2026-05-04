package com.deliveryplatform.profiles.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record ProfileSummary(
        UUID profileId,
        String firstName,
        String lastName,
        BigDecimal avgRating,
        String avatarUrl
) {

    public ProfileSummary withAvatarUrl(String avatarUrl) {
        return new ProfileSummary(profileId, firstName, lastName,
                avgRating, avatarUrl);
    }
}