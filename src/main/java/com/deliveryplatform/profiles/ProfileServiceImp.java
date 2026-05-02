package com.deliveryplatform.profiles;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.profiles.dto.ProfilePatchRequest;
import com.deliveryplatform.profiles.dto.ProfileResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileResolver profileResolver;
    private final ImageService imageService;

    @Override
    public ProfileResponse getUserProfile(UUID userId) {
        return profileResolver.resolve(getByIdOrThrow(userId));
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(UUID userId, ProfilePatchRequest request) {
        var profile = getByIdOrThrow(userId);

        if (request.firstName() != null) profile.setFirstName(request.firstName());
        if (request.lastName() != null)  profile.setLastName(request.lastName());
        if (request.phone() != null)     profile.setPhone(request.phone());

        profileRepository.save(profile);
        return profileResolver.resolve(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateAvatar(UUID userId, UUID imageId) {
        var profile = getByIdOrThrow(userId);
        var image = imageService.getImageEntity(imageId);

        if (!image.isConfirmed()) throw new IllegalStateException("Image not confirmed yet");

        profile.setAvatar(image);
        profileRepository.save(profile);
        return profileResolver.resolve(profile);
    }

    @Override
    @Transactional
    public void removeAvatar(UUID userId) {
        var profile = getByIdOrThrow(userId);
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
        if (profile.getAvatar() == null || !profile.getAvatar().isConfirmed())
            throw new ResourceNotFoundException("User doesn't have an avatar");
    }
}