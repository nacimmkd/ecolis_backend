package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileUpdateRequest;
import com.deliveryplatform.profiles.dto.ProfileDto;

import java.util.UUID;

public interface ProfileService {
    ProfileDto getUserProfile(UUID userId);

    ProfileDto updateProfile(UUID profileId, ProfileUpdateRequest request);

    ProfileDto updateAvatar(UUID profileId, UUID imageId);

    void removeAvatar(UUID userId);

}
