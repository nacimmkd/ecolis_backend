package com.deliveryplatform.profiles;

import org.mapstruct.Mapper;
import com.deliveryplatform.profiles.ProfileDto.*;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toEntity(ProfileRequest profileRequest);
}
