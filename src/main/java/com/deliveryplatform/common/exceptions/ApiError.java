package com.deliveryplatform.common.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApiError {

    private String code;
    private int status;
    private String message;
    private String path;
    private OffsetDateTime timestamp;

    public static ApiError of(DomainException ex, String path) {
        var error = ex.getErrorCode();
        return new ApiError(error.name(), error.status().value() , ex.getMessage() , path, OffsetDateTime.now());
    }

    public static ApiError of(HttpStatus status, String message, String path) {
        return new ApiError(status.name(), status.value() , message, path, OffsetDateTime.now());
    }

}