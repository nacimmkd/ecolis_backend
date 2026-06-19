package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileSummary;
import com.deliveryplatform.profiles.dto.ProfileUpdateRequest;
import com.deliveryplatform.profiles.dto.ProfileDetails;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileDetails> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(profileService.getUserProfile(principal.getId()));
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileDetails> getProfileById(
            @PathVariable UUID profileId
    ) {
        return ResponseEntity.ok(profileService.getUserProfile(profileId));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileSummary> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(principal.getId(), request));
    }

}
