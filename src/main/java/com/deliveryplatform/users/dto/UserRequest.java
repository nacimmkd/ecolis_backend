package com.deliveryplatform.users.dto;

import com.deliveryplatform.profiles.dto.ProfileRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Must not be blank")
        @Email(message = "Valid Email required")
        String email,

        @NotBlank(message = "Must not be blank")
        @Size(min = 5, max = 100)
        String password,

        @Valid
        ProfileRequest profile
) {}