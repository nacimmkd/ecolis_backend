package com.deliveryplatform.common;

import lombok.Builder;

@Builder
public record ValidationError(
        String field,
        String message
) {
}
