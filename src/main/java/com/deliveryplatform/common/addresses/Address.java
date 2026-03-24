package com.deliveryplatform.common.addresses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Address(
        @NotBlank @Size(max = 255) String street,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 20)  String postalCode,
        @NotBlank @Size(max = 60)  String country
) {}