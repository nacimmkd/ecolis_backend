package com.deliveryplatform.storage;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MediaUrlResolver {

    private final StorageService storageService;

    @Named("resolveUrl")
    public String resolveUrl(String key) {
        if (key == null) {
            return null;
        }
        return storageService.generateReadUrl(key);
    }
}