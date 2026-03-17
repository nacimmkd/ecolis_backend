package com.deliveryplatform.users;

import com.deliveryplatform.profiles.ProfileRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(

        @NotBlank(message = "Must not be blank")
        @Email(message = "Valid Email required")
        String email,

        @NotBlank(message = "Must not be blank")
        String password,

        ProfileRequest profile
) {
}
