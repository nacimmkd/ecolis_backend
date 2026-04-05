package com.deliveryplatform.notifications;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "isRead", constant = "false")
    Notification toEntity(NotificationRequest request);
}
