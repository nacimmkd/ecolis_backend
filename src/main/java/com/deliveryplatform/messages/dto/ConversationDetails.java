package com.deliveryplatform.messages.dto;

import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ConversationDetails(
        UUID conversationId,
        List<UserBrief> participants,
        List<MessageSummary> messages,
        OffsetDateTime createdAt
) {}
