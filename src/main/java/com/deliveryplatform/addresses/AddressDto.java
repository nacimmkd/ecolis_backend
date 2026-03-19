package com.deliveryplatform.addresses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record AddressDto() {

    public record AddressRequest(
            @NotBlank @Size(max = 255) String street,
            @NotBlank @Size(max = 100) String city,
            @NotBlank @Size(max = 20)  String postalCode,
            @NotBlank @Size(max = 60)  String country,
            BigDecimal lat,
            BigDecimal lng
    ) {}

    public record AddressResponse(
            String street,
            String city,
            String postalCode,
            String country,
            BigDecimal lat,
            BigDecimal lng
    ) {}
}