package com.deliveryplatform.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank(message = "Must not be blank")
    @Email(message = "Valid Email required")
    private String email;

    @NotBlank(message = "Must not be blank")
    @Size(min = 5, max = 72, message = "Password must be between 5 and 72 characters")
    private String password;
}