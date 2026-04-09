package com.deliveryplatform.common.validations;


import org.springframework.http.HttpStatus;

import java.util.List;

public record ApiValidationError(
        int status,
        String message,
        List<ValidationError> errors
) {

    public record ValidationError(
            String field,
            String message
    ){}

    public static ApiValidationError of(List<ValidationError> errors) {
        return new ApiValidationError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors
        );
    }
}
