package com.deliveryplatform.common.addresses;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    GeoAddress toEntity(Address request);
}
