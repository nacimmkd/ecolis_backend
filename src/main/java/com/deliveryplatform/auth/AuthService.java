package com.deliveryplatform.auth;

import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserPrincipal;

import java.util.UUID;

public interface AuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse refresh(String refreshToken);

    void logout(UUID userId);

    UserPrincipal getCurrentUserPrincipal();

    User getCurrentUser();
}