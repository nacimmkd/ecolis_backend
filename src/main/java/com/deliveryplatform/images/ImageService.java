package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.storage.PresignedUrl;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    PresignedUrl requestImageUpload(String contentType, UUID uploadedBy);
    Image getImageEntity(@NonNull UUID imageId);
    List<Image> getImageEntities(List<UUID> imageIds);
    ImageDto getImage(UUID id);
    String getReadUrl(UUID id);
    ImageDto confirmUpload(String key, UUID uploadedBy);
    void remove(UUID imageId, UUID userId);
    void removeAll(List<UUID> imageIds);
}