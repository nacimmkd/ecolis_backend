package com.deliveryplatform.addresses;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    GeocodedAddress toGeocodedAddress(Address address, double latitude, double longitude);
}