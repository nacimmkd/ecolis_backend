package com.deliveryplatform.auth;

import com.deliveryplatform.auth.jwt.Jwt;
import com.deliveryplatform.users.dto.UserDetails;

public record AuthResponse(
        Jwt accessToken,
        Jwt refreshToken,
        UserDetails user
) {}
