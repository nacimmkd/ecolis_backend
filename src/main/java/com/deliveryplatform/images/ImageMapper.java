package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageMapper {

    private final StorageService storageService;

    public ImageDto toDto(Image image) {
        if (image == null) {
            return null;
        }
        return ImageDto.builder()
                .id(image.getId())
                .url(storageService.generateReadUrl(image.getKey()))
                .mediaType(image.getMediaType())
                .createdAt(image.getCreatedAt())
                .build();
    }

    public List<ImageDto> toDto(List<Image> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream()
                .map(this::toDto)
                .toList();
    }
}
