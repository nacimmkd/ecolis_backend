package com.deliveryplatform.common.addresses;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoAddress {
    private String     street;
    private String     city;
    private String     postalCode;
    private String     country;
    private BigDecimal latitude;
    private BigDecimal longitude;
}