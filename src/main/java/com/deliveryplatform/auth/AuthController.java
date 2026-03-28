package com.deliveryplatform.auth;

import com.deliveryplatform.auth.jwt.JwtResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletResponse response
    ) {
        var login = authService.login(request);

        var cookie = generateCookie(
                login.refreshToken(),
                login.refreshExpiration()
        );

        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(login.accessToken()));
    }


    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue("refresh_token") String refreshToken
    ) {
        var accessToken = authService.refresh(refreshToken);
        return ResponseEntity.ok(new JwtResponse(accessToken));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken);
        var cookie = generateCookie(null,0);
        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @RequestHeader("Authorization") String header
    ) {
        var token = header.replace("Bearer ", "");
        var isValid = authService.validateAccessToken(token);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }

    //-------------------------------------------------------------------

    private Cookie generateCookie(String refreshToken, int expiration) {
        var cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiration);
        return cookie;
    }
}