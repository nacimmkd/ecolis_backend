package com.deliveryplatform.storage;

import com.deliveryplatform.storage.exceptions.StorageErrorCode;
import com.deliveryplatform.storage.exceptions.StorageException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class StorageController {

    private static final String IMAGES_FOLDER_NAME = "images";

    private final StorageService storageService;

    @PostMapping("/presign")
    public ResponseEntity<PresignedUrl> getPresignUrl(@RequestParam("content") @NotBlank String contentType) {
        var mediaType = resolveMediaType(contentType);
        return ResponseEntity.ok(storageService.generatePresignedUrl(mediaType, IMAGES_FOLDER_NAME));
    }

    private MediaType resolveMediaType(String content) {
        return MediaType.of(content)
                .orElseThrow(() -> new StorageException(StorageErrorCode.INVALID_MEDIA_TYPE, "Content type not supported"));
    }
}