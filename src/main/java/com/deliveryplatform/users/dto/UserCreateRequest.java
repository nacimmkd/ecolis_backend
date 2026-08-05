package com.deliveryplatform.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "Must not be blank")
        @Email(message = "Valid Email required")
        String email,

        @NotBlank(message = "Must not be blank")
        @Size(min = 5, max = 100)
        String password,

        @NotBlank(message = "Must not be blank")
        String firstName,

        @NotBlank(message = "Must not be blank")
        String lastName
) {}