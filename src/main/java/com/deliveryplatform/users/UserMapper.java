package com.deliveryplatform.users;

import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.profiles.ProfileMapper;
import com.deliveryplatform.users.dto.UserDetails;
import com.deliveryplatform.users.dto.UserSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ProfileMapper profileMapper;
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
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .avgRating(profile.getAvgRating())
                .avatar(imageMapper.toDto(profile.getAvatar()))
                .verified(user.isVerified())
                .build();
    }
}