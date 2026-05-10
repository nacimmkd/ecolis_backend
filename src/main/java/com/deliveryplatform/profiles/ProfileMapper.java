package com.deliveryplatform.profiles;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.profiles.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final ImageService imageService;

    public ProfileDto toDetailedDto(Profile profile) {
        if (profile == null) {
            return null;
        }
        return ProfileDto.builder()
                .profileId(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phone(profile.getPhone())
                .avgRating(profile.getAvgRating())
                .totalDeliveries(profile.getTotalDeliveries())
                .totalOrders(profile.getTotalOrders())
                .avatarUrl(resolveAvatarUrl(profile.getAvatar()))
                .build();
    }

    private String resolveAvatarUrl(Image avatar) {
        if (avatar == null || avatar.getId() == null) return null;
        return imageService.getReadUrl(avatar.getId());
    }
}