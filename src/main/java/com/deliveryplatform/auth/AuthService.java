package com.deliveryplatform.auth;

import com.deliveryplatform.auth.jwt.JwtConfig;
import com.deliveryplatform.auth.jwt.JwtService;
import com.deliveryplatform.auth.jwt.RefreshTokenService;
import com.deliveryplatform.common.exceptions.AuthenticationException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.users.UserPrincipal;
import com.deliveryplatform.users.UserRepository;
import com.deliveryplatform.users.UserServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        var userPrincipal = (UserPrincipal) auth.getPrincipal();

        var accessToken = jwtService.generateAccessToken(userPrincipal);
        var refreshToken = jwtService.generateRefreshToken(userPrincipal);

        refreshTokenService.save(userPrincipal.getId(), refreshToken);

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtConfig.getRefreshTokenDuration()
        );
    }


    public String refresh(String refreshToken) {
        validateRefreshTokenOrThrow(refreshToken);
        var userPrincipal = getPrincipalFromRefreshToken(refreshToken);

        var newRefreshToken = jwtService.generateRefreshToken(userPrincipal);
        refreshTokenService.save(userPrincipal.getId(), newRefreshToken);

        return jwtService.generateAccessToken(userPrincipal);
    }

    public void logout(UUID userId) {
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

    private UserPrincipal getPrincipalFromRefreshToken(String refreshToken) {
        var userId = jwtService.getUserIdFromToken(refreshToken);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("Session expired, please log in again"));
        return UserPrincipal.from(user);
    }

    private void validateRefreshTokenOrThrow(String refreshToken) {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new AuthenticationException("Session expired");
        }
    }
}