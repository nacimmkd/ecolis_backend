package com.deliveryplatform.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        int refreshExpiration
) {
}
