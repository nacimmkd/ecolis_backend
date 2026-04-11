package com.deliveryplatform.storage;

public interface StorageService {
    PresignedUrl generatePresignedUrl(MediaType mediaType, String folder);
    String generateReadUrl(String key);
    boolean exists(String key);
    void delete(String key);

}
