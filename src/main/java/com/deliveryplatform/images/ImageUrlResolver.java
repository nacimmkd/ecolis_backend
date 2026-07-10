package com.deliveryplatform.images;

import com.deliveryplatform.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageUrlResolver {

    private final StorageService storageService;

    @Named("resolveUrl")
    public String resolveUrl(Image image) {
        if (image == null || image.getKey() == null) {
            return null;
        }
        return storageService.generateReadUrl(image.getKey());
    }
}