package com.deliveryplatform.addresses;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Objects;

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


    public static Address create(AddressRequest request, Coordinates coordinates) {
        return Address.builder()
                .street(request.street())
                .city(request.city())
                .postalCode(request.postalCode())
                .country(request.country())
                .latitude(coordinates.latitude())
                .longitude(coordinates.longitude())
                .build();
    }

    public String toBriefAddress() {return String.format("%s, %s", city, country);}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Double.compare(latitude, address.latitude) == 0 && Double.compare(longitude, address.longitude) == 0 && Objects.equals(street, address.street) && Objects.equals(city, address.city) && Objects.equals(postalCode, address.postalCode) && Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, postalCode, country, latitude, longitude);
    }
}