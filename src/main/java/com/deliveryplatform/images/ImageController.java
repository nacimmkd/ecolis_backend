package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageResponse;
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

    @GetMapping("/{id}")
    public ResponseEntity<ImageResponse> getImage(@PathVariable @NotNull UUID id){
        return ResponseEntity.ok(imageService.getImage(id));
    }

    @PostMapping("/presign")
    public ResponseEntity<PresignedUrl> requestImageUpload(
            @RequestParam("content") @NotBlank String contentType,
            @AuthenticationPrincipal UserPrincipal user) {

        return ResponseEntity.ok(imageService.requestImageUpload(contentType, user.getId()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ImageResponse> confirmUpload(
            @RequestParam("key") @NotBlank String key,
            @AuthenticationPrincipal UserPrincipal user) {

        var response =imageService.confirmUpload(key, user.getId());
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(
            @PathVariable @NotNull UUID id,
            @AuthenticationPrincipal UserPrincipal user) {

        imageService.remove(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}