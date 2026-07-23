package com.deliveryplatform.addresses.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AddressErrorCode implements ErrorCode {

    ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST),
    GEOCODING_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE);

    private final HttpStatus status;

    AddressErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}