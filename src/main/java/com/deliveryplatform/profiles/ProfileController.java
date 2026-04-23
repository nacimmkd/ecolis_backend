package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileRequest;
import com.deliveryplatform.profiles.dto.ProfileResponse;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(profileService.getUserProfile(principal.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid ProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(principal.getId(), request));
    }

    @PatchMapping("/me/avatar")
    public ResponseEntity<ProfileResponse> updateAvatar(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam UUID imageId) {
        return ResponseEntity.ok(profileService.updateAvatar(principal.getId(), imageId));
    }

    @DeleteMapping("/me/avatar")
    public ResponseEntity<Void> removeAvatar(
            @AuthenticationPrincipal UserPrincipal principal) {
        profileService.removeAvatar(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
