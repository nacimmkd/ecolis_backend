package com.deliveryplatform.images;

import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.storage.MediaType;
import com.deliveryplatform.storage.StorageService;
import com.deliveryplatform.storage.PresignedUrl;
import com.deliveryplatform.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImp implements ImageService {

    private final StorageService s3StorageService;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;


    @Override
    public PresignedUrl getPresignUrl(String contentType, UUID uploadedBy) {
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
    public ImageDto confirmUpload(String key, UUID uploadedBy) {
        var image = getByKeyOrThrow(key);
        assertOwnership(image, uploadedBy);
        assertExistsInStorage(key);
        image.setConfirmed(true);
        return imageMapper.toDto(imageRepository.save(image));
    }

    @Override
    public void remove(Image img) {
        var image = getByIdOrThrow(img.getId());
        s3StorageService.delete(image.getKey());
        imageRepository.delete(image);
    }


    @Override
    public void remove(List<Image> images) {
        if (images == null || images.isEmpty()) return;

        var imagesToDelete = imageRepository.findAllById(
                images.stream().map(Image::getId).toList()
        );
        imagesToDelete.forEach(image -> s3StorageService.delete(image.getKey()));
        imageRepository.deleteAll(images);
    }

    @Override
    public Image getImage(UUID imageId, User user) {
        var image = getByIdOrThrow(imageId);
        if (!image.isOwnedBy(user)) throw new UnauthorizedActionException("User is not the owner of image");
        if (!image.isConfirmed()) return null;
        return image;
    }

    @Override
    public List<Image> getImages(List<UUID> imageIds) {
        return imageRepository.findAllById(imageIds);
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
        if (image == null || !image.getUploadedBy().equals(requestedBy)) {
            throw new UnauthorizedActionException("You are not allowed to perform this action");
        }
    }

    private void assertExistsInStorage(String key) {
        if (!s3StorageService.exists(key)) {
            throw new ResourceNotFoundException("Image not found : " + key);
        }
    }
}
