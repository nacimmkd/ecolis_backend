package com.deliveryplatform.addresses;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Address {

    private String street;
    private String city;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;


    public Address withCoordinates(Coordinates coordinates) {
        return Address.builder()
                .street(this.street)
                .city(this.city)
                .postalCode(this.postalCode)
                .country(this.country)
                .latitude(coordinates.latitude())
                .longitude(coordinates.longitude())
                .build();
    }

    public String toFullAddress() {
        return String.format("%s, %s %s, %s", street, postalCode, city, country);
    }
}