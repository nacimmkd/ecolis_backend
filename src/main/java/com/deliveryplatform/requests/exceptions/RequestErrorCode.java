package com.deliveryplatform.requests.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum RequestErrorCode implements ErrorCode {
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND),

    REQUEST_ALREADY_EXISTS(HttpStatus.CONFLICT),
    INVALID_STATE_TRANSITION(HttpStatus.CONFLICT),

    NOT_CARRIER(HttpStatus.FORBIDDEN),
    USER_NOT_INVOLVED(HttpStatus.FORBIDDEN),

    MISSING_REQUIRED_DATA(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    RequestErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}