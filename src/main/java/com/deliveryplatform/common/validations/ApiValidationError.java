package com.deliveryplatform.common.validations;

import com.deliveryplatform.common.exceptions.ApiError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
public class ApiValidationError extends ApiError {

    private static final String CODE = "VALIDATION_ERROR";
    private static final String MESSAGE = "One or more fields are invalid";
    private static final HttpStatus status = HttpStatus.BAD_REQUEST;
    private final List<ValidationError> errors;
    public record ValidationError(String field, String message){}

    private ApiValidationError(List<ValidationError> errors, String path) {
        super(CODE, status.value(), MESSAGE , path, OffsetDateTime.now());
        this.errors = errors;
    }

    public static ApiValidationError of(List<ValidationError> errors, String path) {
        return new ApiValidationError(errors,path);
    }
}
