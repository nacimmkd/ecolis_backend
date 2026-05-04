package com.deliveryplatform.profiles;

import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.profiles.dto.ProfileResponse;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileResolver {

    private final ProfileMapper profileMapper;
    private final ImageService imageService;

    public ProfileResponse resolve(Profile profile) {
        var response = profileMapper.toResponse(profile);
        var avatar = profile.getAvatar();

        if (avatar == null || !avatar.isConfirmed()) return response;

        return response.withAvatarUrl(imageService.getReadUrl(avatar.getId()));
    }


    public ProfileSummary resolveSummary(Profile profile) {
        var response = profileMapper.toSummaryResponse(profile);
        var avatar = profile.getAvatar();

        if (avatar == null || !avatar.isConfirmed()) return response;

        return response.withAvatarUrl(imageService.getReadUrl(avatar.getId()));
    }
}
