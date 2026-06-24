package com.deliveryplatform.addresses;

import com.deliveryplatform.addresses.geocoding.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AddressResolver {

    private final GeocodingService geocodingService;

    public Address resolve(AddressRequest addressRequest) {
        return geocodingService.geocode(addressRequest);
    }
}
