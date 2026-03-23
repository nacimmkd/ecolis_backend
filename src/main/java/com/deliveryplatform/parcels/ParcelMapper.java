package com.deliveryplatform.parcels;

import org.mapstruct.Mapper;
import com.deliveryplatform.parcels.ParcelDto.*;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ParcelMapper {

    ParcelResponse toDto(Parcel parcel);
    Parcel toEntity(ParcelRequest parcelRequest);
    void updateEntity(@MappingTarget Parcel parcel, ParcelRequest request);
}
