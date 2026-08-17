package com.deliveryplatform.parcels.dto;

import jakarta.validation.constraints.NotBlank;

public record ParcelImageRequest(
        @NotBlank String key,
        @NotBlank String contentType
) {}