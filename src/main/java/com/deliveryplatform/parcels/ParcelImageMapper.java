package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.ParcelImageDto;
import com.deliveryplatform.storage.MediaUrlResolver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {MediaUrlResolver.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ParcelImageMapper {

    @Mapping(target = "url", source = "key", qualifiedByName = "resolveUrl")
    @Mapping(target = "content", source = "mediaType.content")
    @Mapping(target = "uploadedAt", source = "createdAt")
    ParcelImageDto toDto(ParcelImage image);

    List<ParcelImageDto> toDto(List<ParcelImage> images);
}