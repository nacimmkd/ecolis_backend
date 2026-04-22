package com.deliveryplatform.users.dto;

import jakarta.validation.constraints.NotBlank;

public record VerificationCodeRequest(
        @NotBlank String code
) {
}
