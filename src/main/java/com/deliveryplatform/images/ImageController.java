package com.deliveryplatform.images;

import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.storage.PresignedUrl;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;


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
}