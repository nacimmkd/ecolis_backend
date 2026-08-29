package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileDto;
import com.deliveryplatform.profiles.dto.ProfileUpdateRequest;
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

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileDto> getProfileById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID profileId
    ) {
        return ResponseEntity.ok(profileService.getProfile(principal.getId(), profileId));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileDto> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(principal.getId(), request));
    }

}
