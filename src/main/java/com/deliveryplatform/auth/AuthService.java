package com.deliveryplatform.auth;

import com.deliveryplatform.users.*;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtConfig jwtConfig;

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        return new LoginResponse(
                accessToken,
                refreshToken,
                jwtConfig.getRefreshTokenExpiration()
        );
    }

    public String refreshToken(String refreshToken) {

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        var userId = jwtService.getUserIdFromToken(refreshToken);

        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return jwtService.generateAccessToken(user);
    }

    public void logout(String refreshToken) {

        var userId = jwtService.getUserIdFromToken(refreshToken);
        refreshTokenService.removeRefreshToken(userId);
    }

    public boolean validateAccessToken(String header) {
        var token = header.replace("Bearer ", "");
        return jwtService.validateAccessToken(token);
    }
}