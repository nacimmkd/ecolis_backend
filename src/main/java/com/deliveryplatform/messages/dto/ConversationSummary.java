package com.deliveryplatform.messages.dto;

import com.deliveryplatform.profiles.dto.ProfileSummary;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record ConversationSummary(
        UUID           conversationId,
        List<ProfileSummary> participants,
        String         lastMessage,
        OffsetDateTime createdAt
) {}
