package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.common.validations.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;


public record ProfilePostRequest(
        @NotBlank(message = "Invalid firstname") @Size(max = 30)
        String firstName,

        @NotBlank(message = "Invalid lastname") @Size(max = 30)
        String lastName,

        @Phone
        String phone,

        UUID avatarId
) {}
