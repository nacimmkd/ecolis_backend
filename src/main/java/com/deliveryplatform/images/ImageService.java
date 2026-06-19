package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.storage.PresignedUrl;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    PresignedUrl getPresignUrl(String contentType, UUID uploadedBy);

    Image getImage(UUID imageId);

    List<Image> getImages(List<UUID> imageIds);

    ImageDto confirmUpload(String key, UUID uploadedBy);

    void remove(Image image, UUID currentUserId);

    void remove(List<Image> images);
}