package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.ParcelCreateRequest;
import com.deliveryplatform.parcels.dto.ParcelDetailedResponse;
import com.deliveryplatform.parcels.dto.ParcelSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParcelMapper {

    @Mapping(target = "parcelId", source = "id")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "thumbnailImageUrl", ignore = true)
    ParcelSummaryResponse toSummaryResponse(Parcel parcel);

    @Mapping(target = "parcelId", source = "id")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "thumbnailImageUrl", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    ParcelDetailedResponse toDetailedResponse(Parcel parcel);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "pickupAddress", ignore = true)
    @Mapping(target = "dropoffAddress", ignore = true)
    @Mapping(target = "thumbnailImage", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Parcel toEntity(ParcelCreateRequest request);
}