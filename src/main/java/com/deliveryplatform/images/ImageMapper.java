package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ImageUrlResolver.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ImageMapper {

    @Mapping(target = "url", source = "image", qualifiedByName = "resolveUrl")
    @Mapping(target = "content", source = "mediaType.content")
    @Mapping(target = "uploadedAt", source = "image.createdAt")
    ImageDto toDto(Image image);

    List<ImageDto> toDto(List<Image> images);
}