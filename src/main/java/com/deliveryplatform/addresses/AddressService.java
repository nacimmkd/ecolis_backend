package com.deliveryplatform.addresses;

public interface AddressService {
    Address geocode(AddressRequest request);
}
