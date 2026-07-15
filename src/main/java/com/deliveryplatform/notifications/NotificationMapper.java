package com.deliveryplatform.notifications;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
interface NotificationMapper {

    @Mapping(target = "notificationId", source = "id")
    @Mapping(target = "isRead", source = "read")
    NotificationDto toDto(Notification notification);

    List<NotificationDto> toDto(List<Notification> notifications);
}
