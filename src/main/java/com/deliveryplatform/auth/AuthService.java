package com.deliveryplatform.auth;

import java.util.UUID;

public interface AuthService {

    AuthResponse login(AuthRequest request);

    String refresh(String refreshToken);

    void logout(UUID userId);

    boolean validateAccessToken(String token);
}