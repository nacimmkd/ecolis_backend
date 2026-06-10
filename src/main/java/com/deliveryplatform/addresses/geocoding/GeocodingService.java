package com.deliveryplatform.addresses.geocoding;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.addresses.Address;

public interface GeocodingService {
    Address geocode(AddressRequest address);
}
