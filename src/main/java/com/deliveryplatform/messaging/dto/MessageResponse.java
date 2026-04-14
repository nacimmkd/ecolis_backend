package com.deliveryplatform.messaging.dto;

import com.deliveryplatform.images.dto.ImageResponse;
import com.deliveryplatform.messaging.Message;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID           id,
        ChatUser       sender,
        String         content,
        List<ImageResponse>   images,
        boolean        read,
        OffsetDateTime sentAt
) {

    public static MessageResponse of(Message message , List<ImageResponse> images) {
        return new MessageResponse(
                message.getId(),
                ChatUser.of(message.getSender()),
                message.getContent(),
                images,
                message.isRead(),
                message.getSentAt()
        );
    }
}