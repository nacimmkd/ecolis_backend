package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileUpdateRequest;
import com.deliveryplatform.profiles.dto.ProfileDto;

import java.util.UUID;

public interface ProfileService {

    ProfileDto getProfile(UUID currentUserId, UUID profileId);

    ProfileDto updateProfile(UUID profileId, ProfileUpdateRequest request);

}
