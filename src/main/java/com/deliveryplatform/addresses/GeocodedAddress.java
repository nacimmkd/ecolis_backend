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
}