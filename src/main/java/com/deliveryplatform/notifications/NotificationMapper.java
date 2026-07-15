package com.deliveryplatform.notifications;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "notificationId", source = "id")
    NotificationDto toDto(Notification notification);

    List<NotificationDto> toDto(List<Notification> notifications);
}
