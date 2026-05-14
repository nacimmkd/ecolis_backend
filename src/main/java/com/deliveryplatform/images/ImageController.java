package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.storage.PresignedUrl;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{imageId}")
    public ResponseEntity<ImageDto> getImage(@PathVariable @NotNull UUID imageId){
        return ResponseEntity.ok(imageService.getImage(imageId));
    }

    @PostMapping("/presign")
    public ResponseEntity<PresignedUrl> getPresignUrl(
            @RequestParam("content") @NotBlank String contentType,
            @AuthenticationPrincipal UserPrincipal user) {

        return ResponseEntity.ok(imageService.getPresignUrl(contentType, user.getId()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ImageDto> confirmUpload(
            @RequestParam("key") @NotBlank String key,
            @AuthenticationPrincipal UserPrincipal user) {

        var response =imageService.confirmUpload(key, user.getId());
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> remove(
            @PathVariable @NotNull UUID imageId,
            @AuthenticationPrincipal UserPrincipal user) {

        imageService.removeImage(imageId, user.getId());
        return ResponseEntity.noContent().build();
    }
}