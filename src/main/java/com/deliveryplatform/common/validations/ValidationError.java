package com.deliveryplatform.validations;

import lombok.Builder;

@Builder
public record ValidationError(
        String field,
        String message
) {
}
