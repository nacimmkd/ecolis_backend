package com.deliveryplatform.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank(message = "Must not be blank")
    @Email(message = "Valid Email required")
    private String email;

    @NotBlank(message = "Must not be blank")
    private String password;
}
