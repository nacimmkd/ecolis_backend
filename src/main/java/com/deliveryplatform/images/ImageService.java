package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.storage.PresignedUrl;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    PresignedUrl getPresignUrl(String contentType, UUID uploadedBy);

    ImageDto getImage(UUID id);

    Image getImageEntity(UUID imageId);

    List<Image> getImageEntities(List<UUID> imageIds);

    String getReadUrl(UUID id); // to be deleted

    ImageDto confirmUpload(String key, UUID uploadedBy);

    void removeImage(UUID imageId, UUID currentUserId);

    void removeAll(List<UUID> imageIds);
}