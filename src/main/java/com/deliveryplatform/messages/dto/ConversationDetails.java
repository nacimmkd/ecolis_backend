package com.deliveryplatform.messages.dto;

import com.deliveryplatform.users.dto.UserSummary;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ConversationDetails(
        UUID conversationId,
        List<UserSummary> participants,
        List<MessageSummary> messages,
        OffsetDateTime createdAt
) {}
