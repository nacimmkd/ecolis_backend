package com.deliveryplatform.addresses;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressMapper {

    public Address toEntity(AddressRequest request) {
        return Address.builder()
                .street(request.street())
                .city(request.city())
                .postalCode(request.postalCode())
                .country(request.country())
                .build();
    }
}
