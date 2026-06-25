package com.deliveryplatform.addresses;

public interface GeocodingPort {
    Coordinates geocode(String fullAddress);
}
