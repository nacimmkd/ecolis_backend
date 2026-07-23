package com.deliveryplatform.storage.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum StorageErrorCode implements ErrorCode {

    FILE_NOT_FOUND(HttpStatus.NOT_FOUND),
    STORAGE_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_MEDIA_TYPE(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    StorageErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}