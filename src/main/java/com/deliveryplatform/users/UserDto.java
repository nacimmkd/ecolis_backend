package com.deliveryplatform.users;

import com.deliveryplatform.profiles.ProfileDto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto() {

    public record UserResponse(
            UUID id,
            String        email,
            Role          role,
            boolean       isVerified,
            boolean       isActive,
            LocalDateTime registeredAt,
            ProfileResponse profile
    ) {}

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

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank @Size(min = 5, max = 100) String newPassword
    ) {}
}
