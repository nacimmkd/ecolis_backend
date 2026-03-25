package com.deliveryplatform.auth;

import com.deliveryplatform.auth.token.JwtConfig;
import com.deliveryplatform.auth.token.JwtService;
import com.deliveryplatform.auth.token.RefreshTokenService;
import com.deliveryplatform.users.*;
import com.deliveryplatform.users.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtConfig jwtConfig;


    public AuthResponse login(AuthRequest request) {

        var auth = authenticate(request.getEmail(), request.getPassword());
        var user = (UserPrincipal) auth.getPrincipal();

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.save(user.getId(), refreshToken);

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtConfig.getRefreshTokenExpiration()
        );
    }


    public String refresh(String refreshToken) {
        validateRefreshTokenOrThrow(refreshToken);
        var user = getUserFromRefreshToken(refreshToken);

        var newRefreshToken = jwtService.generateRefreshToken(user);
        refreshTokenService.save(user.getId(), newRefreshToken);

        return jwtService.generateAccessToken(user);
    }

    public void logout(String refreshToken) {
        validateRefreshTokenOrThrow(refreshToken);
        var userId = jwtService.getUserIdFromToken(refreshToken);
        refreshTokenService.remove(userId);
    }

    public boolean validateAccessToken(String token) {
        return jwtService.validateAccessToken(token);
    }

    // --------------------------------------------------------------------

    private Authentication authenticate(String email, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    }

    private User getUserFromRefreshToken(String refreshToken) {
        var userId = jwtService.getUserIdFromToken(refreshToken);
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private void validateRefreshTokenOrThrow(String refreshToken) {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new AuthenticationSessionException("Session expired");
        }
    }
}