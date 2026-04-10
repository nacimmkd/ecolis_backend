package com.deliveryplatform.storage;

import com.deliveryplatform.storage.dto.PresignedUrlResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;


    @GetMapping("/presign")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(
            @RequestParam("type") @NotBlank String contentType) {

        return ResponseEntity.ok(storageService.generatePresignedUrl(contentType));
    }


    @GetMapping("/read-url")
    public ResponseEntity<String> generateReadUrl(
            @RequestParam("key") @NotBlank String key) {

        return ResponseEntity.ok(storageService.generateReadUrl(key));
    }


    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(
            @RequestParam("key") @NotBlank String key) {

        return ResponseEntity.ok(storageService.exists(key));
    }


    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam("key") @NotBlank String key) {

        storageService.delete(key);
        return ResponseEntity.noContent().build();
    }
}
