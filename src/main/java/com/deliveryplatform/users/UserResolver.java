package com.deliveryplatform.users;

import com.deliveryplatform.profiles.ProfileResolver;
import com.deliveryplatform.users.dto.UserResponse;
import com.deliveryplatform.users.dto.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserResolver {

    private final UserMapper userMapper;
    private final ProfileResolver profileResolver;

    public UserResponse resolve(User user) {
        var profile = profileResolver.resolve(user.getProfile());
        return userMapper.toResponse(user).withProfile(profile);
    }

    public UserSummaryResponse resolveSummary(User user) {
        return userMapper.toSummaryResponse(user);
    }
}