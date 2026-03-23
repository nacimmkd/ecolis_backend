package com.deliveryplatform.common.validations;

import lombok.Builder;

@Builder
public record ValidationError(
        String field,
        String message
) {
}
