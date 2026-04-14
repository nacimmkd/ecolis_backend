package com.deliveryplatform.images.dto;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.storage.MediaType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ImageResponse(
        UUID id,
        String url,
        MediaType mediaType,
        OffsetDateTime createdAt
) {

    public static ImageResponse of(Image image, String url) {
        return new ImageResponse(image.getId(), url, image.getMediaType(), image.getCreatedAt());
    }
}
