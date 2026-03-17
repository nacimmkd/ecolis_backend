package com.deliveryplatform.profiles;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toEntity(ProfileRequest profileRequest);
}
