package com.deliveryplatform.images.dto;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.storage.MediaType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ImageDto(
        UUID id,
        String url,
        MediaType mediaType,
        OffsetDateTime createdAt
) {

    public static ImageDto of(Image image, String url) {
        return new ImageDto(image.getId(), url, image.getMediaType(), image.getCreatedAt());
    }
}
