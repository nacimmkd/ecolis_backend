package com.deliveryplatform.messages.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageImageRequest(
        @NotBlank String key,
        @NotBlank String contentType
) {}