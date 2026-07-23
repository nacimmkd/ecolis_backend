package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.images.exceptions.ImageErrorCode;
import com.deliveryplatform.images.exceptions.ImageException;
import com.deliveryplatform.storage.MediaType;
import com.deliveryplatform.storage.StorageService;
import com.deliveryplatform.storage.PresignedUrl;
import com.deliveryplatform.storage.exceptions.StorageErrorCode;
import com.deliveryplatform.storage.exceptions.StorageException;
import com.deliveryplatform.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImp implements ImageService {

    private final StorageService s3StorageService;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    private static final String IMAGES_FOLDER_NAME = "images";


    @Override
    public PresignedUrl getPresignUrl(String contentType, UUID currentUserId) {
        var mediaType = resolveMediaType(contentType);
        var presignedUrl = s3StorageService.generatePresignedUrl(mediaType, IMAGES_FOLDER_NAME);
        imageRepository.save(Image.create(mediaType, presignedUrl, currentUserId));
        return presignedUrl;
    }

    @Override
    public ImageDto confirmUpload(String key, UUID currentUserId) {
        var image = getByKeyOrThrow(key);
        image.assertOwnedBy(currentUserId);
        assertExistsInStorage(key);
        image.confirm();
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
    public Image getImage(UUID imageId, User currentUser) {
        if (Objects.isNull(imageId)) return null;
        var image = getByIdOrThrow(imageId);
        image.assertOwnedBy(currentUser.getId());
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
                .orElseThrow(() -> new StorageException(StorageErrorCode.INVALID_MEDIA_TYPE, "Content type not supported"));
    }

    private Image getByKeyOrThrow(String key) {
        return imageRepository.findByKey(key)
                .orElseThrow(() -> new ImageException(ImageErrorCode.IMAGE_NOT_FOUND, "Image not found"));
    }

    private Image getByIdOrThrow(UUID id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ImageException(ImageErrorCode.IMAGE_NOT_FOUND, "Image not found"));
    }

    private void assertExistsInStorage(String key) {
        if (!s3StorageService.exists(key)) {
            throw new StorageException(StorageErrorCode.FILE_NOT_FOUND, "Image not found : " + key);
        }
    }
}
