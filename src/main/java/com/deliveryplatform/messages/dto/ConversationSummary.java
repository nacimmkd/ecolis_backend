package com.deliveryplatform.messages.dto;

import com.deliveryplatform.users.dto.UserBrief;
import com.deliveryplatform.users.dto.UserSummary;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ConversationSummary(
        UUID           conversationId,
        List<UserBrief> participants,
        MessageSummary  lastMessage,
        OffsetDateTime createdAt
) {}
