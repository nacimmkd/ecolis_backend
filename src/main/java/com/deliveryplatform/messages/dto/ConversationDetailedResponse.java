package com.deliveryplatform.messages.dto;

import com.deliveryplatform.profiles.dto.ProfileSummary;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ConversationDetailedResponse(
        UUID conversationId,
        ProfileSummary receiver,
        List<MessageResponse> messages,
        OffsetDateTime createdAt
) {
    public ConversationDetailedResponse withReceiver(ProfileSummary receiver) {
        return new ConversationDetailedResponse(conversationId, receiver, messages, createdAt);
    }

    public ConversationDetailedResponse withMessages(List<MessageResponse> messages) {
        return new ConversationDetailedResponse(conversationId, receiver, messages, createdAt);
    }
}
