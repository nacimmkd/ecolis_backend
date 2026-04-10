package com.deliveryplatform.storage.aws;

import com.deliveryplatform.common.exceptions.ExternalServiceException;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.storage.SupportedMediaType;
import com.deliveryplatform.storage.StorageService;
import com.deliveryplatform.storage.dto.PresignedUrlResponse;
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

    private static final String FOLDER = "images";

    @Override
    public PresignedUrlResponse generatePresignedUrl(String content) {

        SupportedMediaType mediaType = SupportedMediaType.of(content)
                .orElseThrow(() -> new InvalidDomainStateException("Invalid content type"));

        String key = FOLDER + "/" + UUID.randomUUID() + mediaType.getExtension();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .contentType(mediaType.getValue())
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r -> r
                .signatureDuration(s3Properties.getPresignDuration())
                .putObjectRequest(putObjectRequest)
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
    public boolean exists(String key) {
        try {
            s3Client.headObject(r -> r.bucket(s3Properties.getBucketName()).key(key));
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("Error while checking existence of key: {}", key, e);
            throw new ExternalServiceException(
                    S3StorageService.class,
                    "Error while checking file existence: " + key
            );
        }
    }

    @Override
    public void delete(String key) {
        try{
            s3Client.deleteObject(r -> r.bucket(s3Properties.getBucketName()).key(key));
        } catch (S3Exception e) {
            log.error("Error while trying to delete file: {}", key);
            throw new ExternalServiceException(
                    S3StorageService.class,
                    "Error while trying to delete file : " + key
            );
        }
    }


}