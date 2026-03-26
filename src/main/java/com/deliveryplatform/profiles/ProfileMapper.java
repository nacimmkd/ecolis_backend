package com.deliveryplatform.profiles;

import com.deliveryplatform.profiles.dto.ProfileRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toEntity(ProfileRequest profileRequest);
}
