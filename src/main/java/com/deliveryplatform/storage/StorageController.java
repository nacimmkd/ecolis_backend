package com.deliveryplatform.storage;


import com.deliveryplatform.storage.dto.PresignedUrlResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;


    @GetMapping("/presign")
    public PresignedUrlResponse generatePresignedUrl(
            @RequestParam("content") @NotBlank String contentType
    ) {
        return storageService.generatePresignedUrl(contentType);
    }


    @PostMapping("/confirm")
    public void confirmUpload(@RequestParam("key") @NotBlank String key) {
        storageService.confirmUpload(key);
    }


    @GetMapping("/read-url")
    public String getReadUrl(@RequestParam("key") @NotBlank String key) {
        return storageService.generateReadUrl(key);
    }


    @DeleteMapping
    public void delete(@RequestParam("key") @NotBlank String key) {
        storageService.delete(key);
    }
}
