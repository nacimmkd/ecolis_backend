package com.deliveryplatform.messages.dto;

import com.deliveryplatform.profiles.dto.ProfileSummary;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ConversationResponse(
        UUID           conversationId,
        List<ProfileSummary> participants,
        String         lastMessage,
        OffsetDateTime createdAt
) {
    public ConversationResponse withParticipants(List<ProfileSummary> participants) {
        return new ConversationResponse(conversationId, participants, lastMessage, createdAt);
    }

    public ConversationResponse withLastMessage(String lastMessage) {
        return new ConversationResponse(conversationId, participants, lastMessage, createdAt);
    }
}
