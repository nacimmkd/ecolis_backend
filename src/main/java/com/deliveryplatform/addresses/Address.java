package com.deliveryplatform.addresses;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String street;
    private String city;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;

    public static Address of(AddressRequest address, double latitude, double longitude) {
        return Address.builder()
                .street(address.street())
                .city(address.city())
                .postalCode(address.postalCode())
                .country(address.country())
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}