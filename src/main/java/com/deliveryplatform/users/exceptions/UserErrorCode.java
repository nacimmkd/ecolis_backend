package com.deliveryplatform.users.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST),
    USER_ALREADY_VERIFIED(HttpStatus.CONFLICT),
    USER_NOT_VERIFIED(HttpStatus.CONFLICT);

    private final HttpStatus status;

    UserErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}