package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageResponse;
import com.deliveryplatform.storage.PresignedUrl;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    PresignedUrl requestImageUpload(String contentType, UUID uploadedBy);
    Image getImageEntity(UUID imageId);
    List<Image> getImageEntities(List<UUID> imageIds);
    ImageResponse getImage(UUID id);
    String getReadUrl(UUID id);
    ImageResponse confirmUpload(String key, UUID uploadedBy);
    void remove(UUID imageId, UUID userId);
    void removeAll(List<UUID> imageIds);
}