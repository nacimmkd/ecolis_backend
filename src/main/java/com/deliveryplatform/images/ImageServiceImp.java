package com.deliveryplatform.images;

import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.storage.MediaType;
import com.deliveryplatform.storage.StorageService;
import com.deliveryplatform.storage.PresignedUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImp implements ImageService {

    private final StorageService s3StorageService;
    private final ImageRepository imageRepository;


    @Override
    public String getImageUrl(String key) {
        return s3StorageService.generateReadUrl(key);
    }

    @Override
    public PresignedUrl requestImageUpload(String contentType , UUID uploadedBy) {
        var mediaType = resolveMediaType(contentType);
        var presignedUrl = s3StorageService.generatePresignedUrl(mediaType, "images");
        imageRepository.save(Image.builder()
                .key(presignedUrl.key())
                .mediaType(mediaType)
                .uploadedBy(uploadedBy)
                .build());
        return presignedUrl;
    }

    @Override
    public void confirmUpload(String key, UUID uploadedBy) {
        var image = getByKeyOrThrow(key);
        assertOwnership(image, uploadedBy);
        image.setConfirmed(true);
    }

    @Override
    public void remove(UUID imageId, UUID userId) {
        var image = getByIdOrThrow(imageId);
        assertOwnership(image,userId);
        s3StorageService.delete(image.getKey());
        imageRepository.delete(image);
    }



    // ------------------------------------------------------

    private MediaType resolveMediaType(String content) {
        return MediaType.of(content)
                .orElseThrow(() -> new InvalidDomainStateException("Content type not supported"));
    }

    private Image getByKeyOrThrow(String key) {
        return imageRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found : " + key));
    }

    private Image getByIdOrThrow(UUID id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
    }

    private void assertOwnership(Image image, UUID requestedBy) {
        if (!image.getUploadedBy().equals(requestedBy)) {
            throw new UnauthorizedActionException("You are not allowed to perform this action");
        }
    }
}
