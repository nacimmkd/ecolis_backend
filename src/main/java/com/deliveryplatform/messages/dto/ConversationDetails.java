package com.deliveryplatform.messages.dto;

import com.deliveryplatform.profiles.dto.ProfileSummary;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record ConversationDetails(
        UUID conversationId,
        List<ProfileSummary> participants,
        List<MessageSummary> messages,
        OffsetDateTime createdAt
) {
    public ConversationDetails withParticipants(List<ProfileSummary> participants) {
        return new ConversationDetails(conversationId, participants, messages, createdAt);
    }

    public ConversationDetails withMessages(List<MessageSummary> messages) {
        return new ConversationDetails(conversationId, participants, messages, createdAt);
    }
}
