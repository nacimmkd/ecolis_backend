package com.deliveryplatform.auth;

import com.deliveryplatform.users.UserPrincipal;
import com.deliveryplatform.users.dto.UserDetails;

import java.util.UUID;

public interface AuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse refresh(String refreshToken);

    void logout(UUID userId);

    UserPrincipal getCurrentUserPrincipal();

    UserDetails getMe();
}