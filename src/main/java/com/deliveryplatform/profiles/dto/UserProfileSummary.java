package com.deliveryplatform.profiles.dto;


import com.deliveryplatform.profiles.Profile;

import java.math.BigDecimal;
import java.util.UUID;

public record UserProfileSummary(
        UUID id,
        String firstName,
        String lastName,
        String avatarUrl,
        BigDecimal avgRating
) {

    public static UserProfileSummary of(Profile profile) {
        return new UserProfileSummary(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                null,
                profile.getAvgRating()
        );
    }


    public static UserProfileSummary of(Profile profile, String avatarUrl) {
        return new UserProfileSummary(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                avatarUrl,
                profile.getAvgRating()
        );
    }
}