package com.deliveryplatform.messages.dto;

import com.deliveryplatform.profiles.dto.ProfileBrief;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ConversationSummary(
        UUID           conversationId,
        List<ProfileBrief> participants,
        MessageSummary  lastMessage,
        OffsetDateTime createdAt
) {}
