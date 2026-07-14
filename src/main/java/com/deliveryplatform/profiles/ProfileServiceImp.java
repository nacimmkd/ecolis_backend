package com.deliveryplatform.profiles;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import com.deliveryplatform.profiles.dto.ProfileUpdateRequest;
import com.deliveryplatform.profiles.dto.ProfileDetails;
import com.deliveryplatform.reviews.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ReviewRepository  reviewRepository;
    private final ProfileMapper     profileMapper;
    private final ImageService      imageService;

    @Override
    public ProfileDetails getUserProfile(UUID userId) {
        var reviews = reviewRepository.findByRevieweeId(userId);
        var profile = getByIdOrThrow(userId);
        return profileMapper.toDetailedDto(profile, reviews);
    }

    @Override
    @Transactional
    public ProfileSummary updateProfile(UUID userId, ProfileUpdateRequest request) {
        var profile = getByIdOrThrow(userId);

        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhone(request.phone());
        updateAvatar(profile, request.avatarId());

        return profileMapper.toSummaryDto(profileRepository.save(profile));
    }

    // ----------------------------------------------------------------

    private Profile getByIdOrThrow(UUID userId) {
        return profileRepository.findProfileById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private void updateAvatar(Profile profile, UUID avatarId) {
        if (avatarId == null) {
            profile.setAvatar(null);
            return;
        }
        // if avatarId in not null, we update avatar with new one
        var image = imageService.getImage(avatarId, profile.getUser());
        profile.setAvatar(image);
    }
}