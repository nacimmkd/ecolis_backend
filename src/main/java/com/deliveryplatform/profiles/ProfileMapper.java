package com.deliveryplatform.profiles;

import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.profiles.dto.ProfileDetails;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final ImageMapper imageMapper;

    public ProfileDetails toDetailedDto(Profile profile) {
        if (profile == null) {
            return null;
        }
        return ProfileDetails.builder()
                .profileId(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phone(profile.getPhone())
                .avgRating(profile.getAvgRating())
                .totalReviews(profile.getTotalReviews())
                .totalDeliveries(profile.getTotalDeliveries())
                .totalOrders(profile.getTotalOrders())
                .avatar(imageMapper.toDto(profile.getAvatar()))
                .build();
    }


    public ProfileSummary toSummaryDto(Profile profile) {
        if (profile == null) {
            return null;
        }
        return ProfileSummary.builder()
                .profileId(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phone(profile.getPhone())
                .avatar(imageMapper.toDto(profile.getAvatar()))
                .build();
    }
}