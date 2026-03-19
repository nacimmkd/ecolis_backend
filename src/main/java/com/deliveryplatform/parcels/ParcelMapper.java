package com.deliveryplatform.parcels;

import org.mapstruct.Mapper;
import com.deliveryplatform.parcels.ParcelDto.*;

@Mapper(componentModel = "spring")
public interface ParcelMapper {
    ParcelResponse toDto(Parcel parcel);
    Parcel toEntity(ParcelRequest parcelRequest);
}
