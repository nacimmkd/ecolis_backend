package com.deliveryplatform.parcels.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ParcelErrorCode implements ErrorCode {

    PARCEL_NOT_FOUND(HttpStatus.NOT_FOUND),
    PARCEL_NOT_OWNED(HttpStatus.FORBIDDEN),
    PARCEL_NOT_AVAILABLE(HttpStatus.CONFLICT),
    PARCEL_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND),
    PARCEL_INVALID_STATE(HttpStatus.CONFLICT);

    private final HttpStatus status;

    ParcelErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}