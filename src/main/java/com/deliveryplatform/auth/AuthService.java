package com.deliveryplatform.auth;

import com.deliveryplatform.auth.tokens.RefreshTokenService;
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
        refreshTokenService.save(user.getId(), refreshToken);
        return new LoginResponse(
                accessToken,
                refreshToken,
                jwtConfig.getRefreshTokenExpiration()
        );
    }

    public String refresh(String refreshToken) {

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        var userId = jwtService.getUserIdFromToken(refreshToken);

        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return jwtService.generateAccessToken(user);
    }

    public void logout(String refreshToken) {
        var userId = jwtService.getUserIdFromToken(refreshToken);
        refreshTokenService.remove(userId);
    }

    public boolean validateAccessToken(String token) {
        return jwtService.validateAccessToken(token);
    }
}