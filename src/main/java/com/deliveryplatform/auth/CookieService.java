package com.deliveryplatform.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
public class CookieService {

    public final static String REFRESH_COOKIE_NAME = "REFRESH_TOKEN";
    public final static String ACCESS_COOKIE_NAME = "ACCESS_TOKEN";
    public final static String REFRESH_PATH = "/api/v1/auth";

    public void setAuthCookies(HttpServletResponse response, AuthResponse authResponse) {
        addCookie(response, ACCESS_COOKIE_NAME, "/",
                authResponse.accessToken().token(), authResponse.accessToken().expiresInSeconds());
        addCookie(response, REFRESH_COOKIE_NAME, REFRESH_PATH,
                authResponse.refreshToken().token(), authResponse.refreshToken().expiresInSeconds());
    }

    public void clearAuthCookies(HttpServletResponse response) {
        addCookie(response, ACCESS_COOKIE_NAME, "/", "", Duration.ofMillis(0));
        addCookie(response, REFRESH_COOKIE_NAME, REFRESH_PATH, "", Duration.ofMillis(0));
    }

    private void addCookie(HttpServletResponse response, String name,String path, String value, Duration maxAgeSeconds) {
        var cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(path)
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}