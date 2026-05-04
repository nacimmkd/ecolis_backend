package com.deliveryplatform.messages.dto;

import com.deliveryplatform.profiles.dto.ProfileSummary;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConversationResponse(
        UUID           conversationId,
        ProfileSummary receiver,
        String         lastMessage,
        OffsetDateTime createdAt
) {
    public ConversationResponse withReceiver(ProfileSummary receiver) {
        return new ConversationResponse(conversationId, receiver, lastMessage, createdAt);
    }

    public ConversationResponse withLastMessage(String lastMessage) {
        return new ConversationResponse(conversationId, receiver, lastMessage, createdAt);
    }
}
