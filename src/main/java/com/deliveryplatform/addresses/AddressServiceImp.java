package com.deliveryplatform.addresses;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImp implements AddressService {

    private final GeocodingPort geocoding;
    private final AddressMapper addressMapper;


    @Override
    public Address geocode(AddressRequest request) {
        var address = addressMapper.toEntity(request);
        var coordinates = geocoding.geocode(address.getFullAddress());
        return address.withCoordinates(coordinates);
    }
}
