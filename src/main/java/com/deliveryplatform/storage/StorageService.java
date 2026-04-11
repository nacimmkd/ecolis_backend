package com.deliveryplatform.storage;

import com.deliveryplatform.storage.dto.PresignedUrlResponse;

public interface StorageService {
    PresignedUrlResponse generatePresignedUrl(String contentType);
    String generateReadUrl(String key);
    void confirmUpload(String key);
    void delete(String key);

}
