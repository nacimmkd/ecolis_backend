package com.deliveryplatform.profiles.dto;


import com.deliveryplatform.profiles.Profile;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfileSummaryResponse(
        UUID id,
        String firstName,
        String lastName,
        String avatarUrl,
        BigDecimal avgRating
) {

    public static ProfileSummaryResponse of(Profile profile) {
        return new ProfileSummaryResponse(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                null,
                profile.getAvgRating()
        );
    }


    public static ProfileSummaryResponse of(Profile profile, String avatarUrl) {
        return new ProfileSummaryResponse(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                avatarUrl,
                profile.getAvgRating()
        );
    }
}