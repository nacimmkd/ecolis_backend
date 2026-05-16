package com.deliveryplatform.profiles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileStatsResolver {

    private final ProfileRepository profileRepository;

    public Double resolveAvgRating(Profile profile) {
        return profileRepository.getAvgRating(profile.getId());
    }
}
