package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileAvatarRequest;
import com.deliveryplatform.profiles.dto.ProfileUpdateRequest;
import com.deliveryplatform.profiles.dto.ProfileDto;
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
    public ResponseEntity<ProfileDto> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(profileService.getUserProfile(principal.getId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<ProfileDto> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(principal.getId(), request));
    }

    @PatchMapping("/me/avatar")
    public ResponseEntity<ProfileDto> updateAvatar(
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
