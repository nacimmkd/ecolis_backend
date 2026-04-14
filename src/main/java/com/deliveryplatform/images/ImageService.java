package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageResponse;
import com.deliveryplatform.storage.PresignedUrl;

import java.util.UUID;

public interface ImageService {

    PresignedUrl requestImageUpload(String contentType, UUID uploadedBy);
    ImageResponse getImage(String key);
    ImageResponse confirmUpload(String key, UUID uploadedBy);
    void remove(UUID imageId, UUID userId);
}