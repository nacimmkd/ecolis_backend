package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.common.validations.Phone;
import com.deliveryplatform.profiles.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfilePatchRequest(
        @NotBlank(message = "Invalid firstname") @Size(max = 100)
        String firstName,

        @NotBlank(message = "Invalid lastname") @Size(max = 100)
        String lastName,

        @Phone
        String phone
) {

    public static Profile toEntity(ProfilePatchRequest request) {
        return Profile.builder()
                .firstName(request.firstName)
                .lastName(request.lastName)
                .phone(request.phone)
                .avgRating(null)
                .totalDeliveries(0)
                .totalOrders(0)
                .avatar(null)
                .build();
    }
}
