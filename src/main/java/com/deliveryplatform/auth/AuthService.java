package com.deliveryplatform.auth;

import com.deliveryplatform.users.*;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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


    public Login login(LoginRequest request) {

        authenticate(request.getEmail(), request.getPassword());

        var user = findUserByEmail(request.getEmail());

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.save(user.getId(), refreshToken);

        return new Login(
                accessToken,
                refreshToken,
                jwtConfig.getRefreshTokenExpiration()
        );
    }


    public String refresh(String refreshToken) {
        validateRefreshToken(refreshToken);
        var user = getUserFromRefreshToken(refreshToken);
        return jwtService.generateAccessToken(user);
    }

    public void logout(String refreshToken) {
        var userId = jwtService.getUserIdFromToken(refreshToken);
        refreshTokenService.remove(userId);
    }

    public boolean validateAccessToken(String token) {
        return jwtService.validateAccessToken(token);
    }

    // --------------------------------------------------------------------

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private User getUserFromRefreshToken(String refreshToken) {
        var userId = jwtService.getUserIdFromToken(refreshToken);
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private void validateRefreshToken(String refreshToken) {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
    }
}