package com.deliveryplatform.messages.dto;


import com.deliveryplatform.messages.Conversation;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ConversationDetailedResponse(
        UUID id,
        ChatUser receiver,
        List<MessageResponse> messages,
        OffsetDateTime createdAt
) {


    public static ConversationDetailedResponse of(Conversation conversation, ChatUser receiver, List<MessageResponse> messages) {
        return ConversationDetailedResponse.builder()
                .id(conversation.getId())
                .receiver(receiver)
                .messages(messages)
                .createdAt(conversation.getCreatedAt())
                .build();
    }
}
