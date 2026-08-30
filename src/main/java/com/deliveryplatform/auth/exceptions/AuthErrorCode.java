package com.deliveryplatform.auth.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "Auth error codes")
public enum AuthErrorCode implements ErrorCode {

    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED),
    MISSING_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED),
    USER_NOT_VERIFIED(HttpStatus.UNAUTHORIZED),
    USER_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED);

    private final HttpStatus status;

    AuthErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus status() { return status; }
}