package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileSummary;
import com.deliveryplatform.profiles.dto.ProfileUpdateRequest;
import com.deliveryplatform.profiles.dto.ProfileDetails;

import java.util.UUID;

public interface ProfileService {
    ProfileDetails getUserProfile(UUID userId);

    ProfileSummary updateProfile(UUID profileId, ProfileUpdateRequest request);

    ProfileSummary updateAvatar(UUID profileId, UUID imageId);

    void removeAvatar(UUID userId);

}
