package com.deliveryplatform.parcels;

import org.mapstruct.Mapper;
import com.deliveryplatform.parcels.ParcelDto.*;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ParcelMapper {

    @Mapping(source = "user.id",target = "userId")
    ParcelResponse toDto(Parcel parcel);
    Parcel toEntity(ParcelRequest parcelRequest);
    void updateEntity(@MappingTarget Parcel parcel, ParcelRequest request);
}
