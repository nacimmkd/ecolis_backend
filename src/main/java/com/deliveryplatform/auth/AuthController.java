package com.deliveryplatform.auth;

import com.deliveryplatform.auth.jwt.JwtResponse;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthServiceImp authService;

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
            @CookieValue("REFRESH_TOKEN") String refreshToken,
            HttpServletResponse response
    ) {
        var authResponse = authService.refresh(refreshToken);

        var cookie = generateCookie(
                authResponse.refreshToken(),
                authResponse.refreshExpiration()
        );

        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(authResponse.accessToken()));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response
    ) {
        authService.logout(principal.getId());
        var cookie = generateCookie(null, 0);
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
        var cookie = new Cookie("REFRESH_TOKEN", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiration);
        return cookie;
    }
}