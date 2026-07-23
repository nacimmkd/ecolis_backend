package com.deliveryplatform.auth.jwt;

import java.time.Duration;

public record Jwt(
        String token,
        Duration expiresInSeconds
) {}
