package com.deliveryplatform.users.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestEmailVerification(
        @NotBlank String email
) {
}
