package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileAvatarRequest;
import com.deliveryplatform.profiles.dto.ProfilePatchRequest;
import com.deliveryplatform.profiles.dto.ProfilePostRequest;
import com.deliveryplatform.profiles.dto.ProfileResponse;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(profileService.getUserProfile(principal.getId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid ProfilePatchRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(principal.getId(), request));
    }

    @PatchMapping("/me/avatar")
    public ResponseEntity<ProfileResponse> updateAvatar(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ProfileAvatarRequest request) {
        return ResponseEntity.ok(profileService.updateAvatar(principal.getId(), request.avatarId()));
    }

    @DeleteMapping("/me/avatar")
    public ResponseEntity<Void> removeAvatar(
            @AuthenticationPrincipal UserPrincipal principal) {
        profileService.removeAvatar(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
