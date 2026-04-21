package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.profiles.Profile;

import java.math.BigDecimal;

public record ProfileResponse(
        String firstName,
        String lastName,
        String phone,
        BigDecimal avgRating,
        int totalDeliveries,
        int totalOrders,
        String iban
){
    public static ProfileResponse of(Profile profile) {
        return new ProfileResponse(
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                profile.getAvgRating(),
                profile.getTotalDeliveries(),
                profile.getTotalOrders(),
                profile.getIban()
        );
    }
}