package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.ParcelRequest;
import com.deliveryplatform.parcels.dto.ParcelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ParcelMapper {

    @Mapping(source = "user.id",target = "userId")
    ParcelResponse toDto(Parcel parcel);
    Parcel toEntity(ParcelRequest parcelRequest);
    void updateEntity(@MappingTarget Parcel parcel, ParcelRequest request);
}
