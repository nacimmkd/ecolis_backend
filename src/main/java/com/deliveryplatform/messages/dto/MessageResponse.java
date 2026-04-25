package com.deliveryplatform.messages.dto;

import com.deliveryplatform.images.dto.ImageResponse;
import com.deliveryplatform.messages.Message;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID           id,
        ChatUser       sender,
        String         content,
        List<String>   imagesUrls,
        OffsetDateTime sentAt
) {

    public static MessageResponse of(Message message, ChatUser sender , List<String> images) {
        return new MessageResponse(
                message.getId(),
                sender,
                message.getContent(),
                images,
                message.getSentAt()
        );
    }
}