package com.deliveryplatform.addresses;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImp implements AddressService {

    private final GeocodingPort geocoding;


    @Override
    public Address geocode(AddressRequest request) {
        var coordinates = geocoding.geocode(request.toString());
        return Address.create(request, coordinates);
    }
}
