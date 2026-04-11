package com.deliveryplatform.images;

import com.deliveryplatform.storage.PresignedUrl;

import java.util.UUID;

public interface ImageService {

    PresignedUrl requestImageUpload(String contentType, UUID uploadedBy);
    String getImageUrl(String key);
    void confirmUpload(String key, UUID uploadedBy);
    void remove(UUID imageId, UUID userId);
}