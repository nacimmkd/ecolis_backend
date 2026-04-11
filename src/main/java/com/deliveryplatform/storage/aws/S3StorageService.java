package com.deliveryplatform.storage.aws;

import com.deliveryplatform.common.exceptions.ExternalServiceException;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.storage.Image;
import com.deliveryplatform.storage.ImageType;
import com.deliveryplatform.storage.ImageRepository;
import com.deliveryplatform.storage.StorageService;
import com.deliveryplatform.storage.dto.PresignedUrlResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    private final ImageRepository imageRepository;


    @Override
    @Transactional
    public PresignedUrlResponse generatePresignedUrl(String content) {

        ImageType mediaType = resolveMediaType(content);
        String key = generateKey(mediaType);

        PresignedPutObjectRequest presignedRequest = createPresignedRequest(key, mediaType);

        imageRepository.save(Image.builder()
                .key(key)
                .contentType(mediaType)
                .build()
        );

        return PresignedUrlResponse.of(
                presignedRequest.url().toString(),
                key,
                s3Properties.getPresignDuration()
        );
    }


    @Override
    public String generateReadUrl(String key) {
        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(r -> r
                .signatureDuration(s3Properties.getReadDuration())
                .getObjectRequest(g -> g
                        .bucket(s3Properties.getBucketName())
                        .key(key)
                )
        );
        return presigned.url().toString();
    }


    @Override
    @Transactional
    public void confirmUpload(String key) {
        assertMediaExistsInS3(key);
        Image image = getByKeyOrThrow(key);

        if (image.isConfirmed()) throw new InvalidDomainStateException("Image is already confirmed");

        image.setConfirmed(true);
        imageRepository.save(image);
    }


    @Override
    public void delete(String key) {
        Image image = getByKeyOrThrow(key);
        try{
            s3Client.deleteObject(r -> r.bucket(s3Properties.getBucketName()).key(key));
            imageRepository.delete(image);
        } catch (S3Exception e) {
            log.error("Error while trying to delete file: {}", key);
            throw new ExternalServiceException(
                    S3StorageService.class,
                    "Error while trying to delete file : " + key
            );
        }
    }


    //---------------------------------------------------------------------

    private Image getByKeyOrThrow(String key) {
        return imageRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + key));
    }

    private ImageType resolveMediaType(String content) {
        return ImageType.of(content)
                .orElseThrow(() -> new InvalidDomainStateException("Invalid content type"));
    }

    private String generateKey(ImageType mediaType) {
        return "images/" + UUID.randomUUID() + mediaType.getExtension();
    }

    private PresignedPutObjectRequest createPresignedRequest(String key, ImageType mediaType) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .contentType(mediaType.getValue())
                .build();

        return s3Presigner.presignPutObject(r -> r
                .signatureDuration(s3Properties.getPresignDuration())
                .putObjectRequest(putObjectRequest)
        );
    }

    private void assertMediaExistsInS3(String key){
        try {
            s3Client.headObject(r -> r.bucket(s3Properties.getBucketName()).key(key));
        } catch (NoSuchKeyException e) {
            throw new ResourceNotFoundException("Image not found : " + key);
        } catch (S3Exception e) {
            log.error("Error while checking existence of key: {}", key, e);
            throw new ExternalServiceException(
                    S3StorageService.class,
                    "Error while checking file existence: " + key
            );
        }
    }


}