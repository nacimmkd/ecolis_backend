package com.deliveryplatform.profiles.dto;

import com.deliveryplatform.common.validations.Phone;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ProfileUpdateRequest(
        @NotNull @Size(max = 100)
        String firstName,

        @NotNull @Size(max = 100)
        String lastName,

        @NotNull @Phone
        String phone,

        UUID avatarId
) {}
