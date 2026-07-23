package com.deliveryplatform.common.exceptions;

import com.deliveryplatform.common.validations.ApiValidationError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- DOMAIN ------------------------------------------------------------

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(DomainException ex, HttpServletRequest req) {
        log.warn("Domain error [{}] on {}: {}", ex.getErrorCode().name(), req.getRequestURI(), ex.getMessage());
        var error = ApiError.of(ex, req.getRequestURI());
        return ResponseEntity.status(ex.getErrorCode().status()).body(error);
    }

    // ---- AUTH ------------------------------------------------------------

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ApiError> handleMissingCookie(MissingRequestCookieException ex, HttpServletRequest req) {
        log.warn("Missing cookie [{}] on {}", ex.getCookieName(), req.getRequestURI());
        var error = ApiError.of(HttpStatus.BAD_REQUEST, "Missing required cookie", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        log.warn("Invalid email or password");
        var error = ApiError.of(HttpStatus.UNAUTHORIZED, "Invalid email or password", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }


    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAuthDenied(AuthorizationDeniedException ex, HttpServletRequest req) {
        log.warn("Access denied on {}", req.getRequestURI());
        var error = ApiError.of(HttpStatus.FORBIDDEN, "Access denied", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // ---- VALIDATIONS ------------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiValidationError> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ){
        List<ApiValidationError.ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiValidationError.ValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity.badRequest().body(
                ApiValidationError.of(errors, req.getRequestURI())
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {}", request.getRequestURI(), ex);
        var error = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}