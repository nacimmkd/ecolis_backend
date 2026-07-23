package com.deliveryplatform.auth;

import com.deliveryplatform.auth.jwt.Jwt;

public record AuthResponse(
        Jwt accessToken,
        Jwt refreshToken
) {}
