package com.deliveryplatform.addresses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Address(
        @NotBlank @Size(max = 255) String street,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 20)  String postalCode,
        @NotBlank @Size(max = 60)  String country
) {

    @Override
    public String toString(){
        return "%s, %s %s, %s".formatted(street, postalCode, city, country);
        // → "10 rue de la Paix, 75001 Paris, France"
    }
}