package com.deliveryplatform.addresses;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeocodedAddress {
    private String street;
    private String city;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;

    public static GeocodedAddress of(Address address, double latitude, double longitude) {
        return GeocodedAddress.builder()
                .street(address.street())
                .city(address.city())
                .postalCode(address.postalCode())
                .country(address.country())
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}