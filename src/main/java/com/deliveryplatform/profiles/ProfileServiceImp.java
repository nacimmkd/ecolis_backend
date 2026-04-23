package com.deliveryplatform.profiles;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.profiles.dto.ProfileRequest;
import com.deliveryplatform.profiles.dto.ProfileResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ImageService imageService;

    @Override
    public ProfileResponse getUserProfile(UUID userId) {
        var profile = getByIdOrThrow(userId); // userId and profileId are the same

        if (profile.getAvatar() != null && profile.getAvatar().isConfirmed()) {
            var avatarUrl = imageService.getReadUrl(profile.getAvatar().getId());
            return ProfileResponse.of(profile, avatarUrl);
        }

        return ProfileResponse.of(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(UUID userId, ProfileRequest request) {
        var profile = getByIdOrThrow(userId); // userId and profileId are the same

        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhone(request.phone());

        profileRepository.save(profile);
        return ProfileResponse.of(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateAvatar(UUID userId, UUID imageId) {
        var profile = getByIdOrThrow(userId); // userId and profileId are the same
        var image = imageService.getImageEntity(imageId);

        if (!image.isConfirmed()) {
            throw new IllegalStateException("Image not confirmed yet");
        }

        profile.setAvatar(image);
        return ProfileResponse.of(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public void removeAvatar(UUID userId) {
        var profile = getByIdOrThrow(userId); // userId and profileId are the same
        assertAvatarExists(profile);
        var avatarId = profile.getAvatar().getId();
        profile.setAvatar(null);
        profileRepository.save(profile);
        imageService.remove(avatarId, userId);
    }

    // ----------------------------------------------------------------

    private Profile getByIdOrThrow(UUID userId) {
        return profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private void assertAvatarExists(Profile profile) {
        if (profile.getAvatar() == null || !profile.getAvatar().isConfirmed()) {
            throw new ResourceNotFoundException("User doesn't have an avatar");
        }
    }

}