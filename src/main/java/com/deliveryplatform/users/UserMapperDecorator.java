package com.deliveryplatform.users;

import com.deliveryplatform.profiles.ProfileMapper;
import com.deliveryplatform.users.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class UserMapperDecorator implements UserMapper {

    @Autowired
    private UserMapper delegate;

    @Autowired
    private ProfileMapper profileMapper;


    @Override
    public UserDto toDto(User user) {

        UserDto dto = delegate.toDto(user);

        return dto.toBuilder()
                .profile(
                        profileMapper.toDetailedDto(
                                user.getProfile()
                        )
                )
                .build();
    }
}