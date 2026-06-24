package com.deliveryplatform.addresses;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressMapper {

    private final AddressResolver resolver;

    public Address toEntity(AddressRequest addressRequest) {
        return resolver.resolve(addressRequest);
    }
}
