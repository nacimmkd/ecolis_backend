package com.deliveryplatform.messages.dto;

import com.deliveryplatform.profiles.dto.ProfileBrief;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ConversationDetails(
        UUID conversationId,
        List<ProfileBrief> participants,
        List<MessageSummary> messages,
        OffsetDateTime createdAt
) {}
