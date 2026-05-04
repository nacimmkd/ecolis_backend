package com.deliveryplatform.messages.dto;


import com.deliveryplatform.profiles.dto.ProfileSummary;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID           messageId,
        ProfileSummary sender,
        String         content,
        List<String>   imagesUrls,
        OffsetDateTime sentAt
) {
    public MessageResponse withSender(ProfileSummary sender) {
        return new MessageResponse(messageId, sender, content, imagesUrls, sentAt);
    }

    public MessageResponse withImagesUrls(List<String> imagesUrls) {
        return new MessageResponse(messageId, sender, content, imagesUrls, sentAt);
    }
}