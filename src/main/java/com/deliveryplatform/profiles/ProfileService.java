package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfilePatchRequest;
import com.deliveryplatform.profiles.dto.ProfileResponse;

import java.util.UUID;

public interface ProfileService {
    ProfileResponse getUserProfile(UUID userId);

    ProfileResponse updateProfile(UUID profileId, ProfilePatchRequest request);

    ProfileResponse updateAvatar(UUID profileId, UUID imageId);

    void removeAvatar(UUID userId);

}
