package com.deliveryplatform.profiles;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.profiles.dto.ProfileDetails;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class ProfileMapperDecorator implements ProfileMapper {

    @Autowired
    private ProfileMapper delegate;

    @Autowired
    private ImageService imageService;


    @Override
    public ProfileDetails toDetailedDto(Profile profile) {
        return delegate.toDetailedDto(profile)
                .toBuilder()
                .avatarUrl(resolveAvatarUrl(profile.getAvatar()))
                .build();
    }

    @Override
    public ProfileSummary toSummaryDto(Profile profile) {
        return delegate.toSummaryDto(profile)
                .toBuilder()
                .avatarUrl(resolveAvatarUrl(profile.getAvatar()))
                .build();
    }

    private String resolveAvatarUrl(Image avatar) {
        if (avatar == null || avatar.getId() == null) return null;
        return imageService.getReadUrl(avatar.getId());
    }
}
