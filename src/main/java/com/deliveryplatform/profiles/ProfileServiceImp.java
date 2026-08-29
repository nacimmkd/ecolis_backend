package com.deliveryplatform.profiles;

import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.bookings.BookingState;
import com.deliveryplatform.profiles.dto.ProfileUpdateRequest;
import com.deliveryplatform.profiles.dto.ProfileDto;
import com.deliveryplatform.profiles.exceptions.ProfileErrorCode;
import com.deliveryplatform.profiles.exceptions.ProfileException;
import com.deliveryplatform.storage.StorageService;
import com.deliveryplatform.storage.exceptions.StorageErrorCode;
import com.deliveryplatform.storage.exceptions.StorageException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    private final ProfileRepository profileRepository;
    private final BookingRepository bookingRepository;
    private final ProfileMapper     profileMapper;
    private final StorageService    storageService;

    @Override
    public ProfileDto getProfile(UUID currentUserId, UUID profileId) {
        var profile = getByIdOrThrow(profileId);
        var profileDto = profileMapper.toDetailedDto(profile);

        if (currentUserId.equals(profileId)) {
            return profileDto;
        }

        var showPhoneNumber = bookingRepository.existsBookingBetweenUsers(currentUserId, profileId, List.of(BookingState.ACCEPTED));
        if (showPhoneNumber) {
            return profileDto;
        }

        return profileDto.toBuilder()
                .phone(null)
                .phoneVisible(false)
                .build();
    }

    @Override
    @Transactional
    public ProfileDto updateProfile(UUID userId, ProfileUpdateRequest request) {
        var profile = getByIdOrThrow(userId);

        // actualy no phone verification system so i did like this
        boolean isVerified = request.phone() != null;

        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhone(request.phone());
        profile.setCountry(request.country());
        profile.setVerified(isVerified);
        updateAvatar(profile, request.avatarKey());

        return profileMapper.toDetailedDto(profileRepository.save(profile));
    }

    // ----------------------------------------------------------------

    private Profile getByIdOrThrow(UUID userId) {
        return profileRepository.findProfileById(userId)
                .orElseThrow(() -> new ProfileException(ProfileErrorCode.PROFILE_NOT_FOUND, "Profile not found"));
    }

    private void updateAvatar(Profile profile, String avatarKey) {
        if (avatarKey == null) {
            return;
        }
        if (!storageService.exists(avatarKey)) {
            throw new StorageException(StorageErrorCode.FILE_NOT_FOUND, "Image not found : " + avatarKey);
        }
        profile.setAvatarKey(avatarKey);
    }
}