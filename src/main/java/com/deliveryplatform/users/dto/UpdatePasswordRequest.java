package com.deliveryplatform.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 5, max = 100) String newPassword
) {}