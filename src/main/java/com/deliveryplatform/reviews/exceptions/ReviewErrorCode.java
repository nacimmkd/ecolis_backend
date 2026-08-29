package com.deliveryplatform.reviews.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ReviewErrorCode implements ErrorCode {

    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND),
    SELF_REVIEW_NOT_ALLOWED(HttpStatus.CONFLICT);

    private final HttpStatus status;

    ReviewErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}