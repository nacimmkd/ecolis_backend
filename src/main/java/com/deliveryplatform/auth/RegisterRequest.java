package com.deliveryplatform.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(

        @NotBlank(message = "Must not be blank")
        @Email(message = "Valid Email required")
        String email,

        @NotBlank(message = "Must not be blank")
        String password
) {
}
