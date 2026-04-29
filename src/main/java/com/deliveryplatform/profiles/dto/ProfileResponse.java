package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.profiles.Profile;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfileResponse(
        UUID profileId,
        String firstName,
        String lastName,
        String phone,
        String avatarUrl,
        BigDecimal avgRating,
        int totalDeliveries,
        int totalOrders
) {

    public static ProfileResponse of(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                null,
                profile.getAvgRating(),
                profile.getTotalDeliveries(),
                profile.getTotalOrders()
        );
    }


    public static ProfileResponse of(Profile profile, String avatarUrl) {
        return new ProfileResponse(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                avatarUrl,
                profile.getAvgRating(),
                profile.getTotalDeliveries(),
                profile.getTotalOrders()
        );
    }
}