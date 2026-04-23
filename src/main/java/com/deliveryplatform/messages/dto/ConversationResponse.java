package com.deliveryplatform.messages.dto;


import com.deliveryplatform.messages.Conversation;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConversationResponse(
        UUID            id,
        ChatUser        receiver,
        String          lastMessage,
        OffsetDateTime  createdAt
){

    public static ConversationResponse of(Conversation conversation, ChatUser receiver) {
        var lastMessage = conversation.getLastMessage();
        return new ConversationResponse(
                conversation.getId(),
                receiver,
                lastMessage != null ? lastMessage.getContent() : "image",
                conversation.getCreatedAt()
        );
    }
}
