package com.deliveryplatform.users;

import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.profiles.ProfileMapper;
import com.deliveryplatform.profiles.ProfileStatsResolver;
import com.deliveryplatform.users.dto.UserDetails;
import com.deliveryplatform.users.dto.UserBrief;
import com.deliveryplatform.users.dto.UserSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ProfileMapper profileMapper;
    private final ProfileStatsResolver resolver;
    private final ImageMapper imageMapper;

    public UserDetails toDetailsDto(User user) {
        return UserDetails.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .verified(user.isVerified())
                .profile(profileMapper.toSummaryDto(user.getProfile()))
                .registeredAt(user.getRegisteredAt())
                .build();
    }

    public UserSummary toSummaryDto(User user) {
        if (user == null) return null;

        var profile = user.getProfile();
        if (profile == null) return null;
        return UserSummary.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .verified(user.isVerified())
                .registeredAt(user.getRegisteredAt())
                .build();
    }

    public UserBrief toRefDto(User user) {
        var profile = user.getProfile();
        return UserBrief.builder()
                .userId(user.getId())
                .firstName(profile != null ? profile.getFirstName() : null)
                .lastName(profile != null ? profile.getLastName() : null)
                .avatar(
                        profile != null ? imageMapper.toDto(profile.getAvatar()) : null
                )
                .avgRating(
                        profile != null ? resolver.resolveAvgRating(profile) : null
                )
                .verified(user.isVerified())
                .build();

    }
}