package com.deliveryplatform.messaging.dto;


import com.deliveryplatform.messaging.Conversation;
import com.deliveryplatform.users.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConversationResponse(
        UUID            id,
        UUID            bookingId,
        ChatUser        receiver,
        String          lastMessage,
        long            unreadMessagesCount,
        OffsetDateTime  createdAt
){

    public static ConversationResponse of(Conversation conversation, User receiver) {
        var lastMessage = conversation.getLastMessage();
        return new ConversationResponse(
                conversation.getId(),
                conversation.getBooking().getId(),
                ChatUser.of(receiver),
                lastMessage != null ? lastMessage.getContent() : "image",
                conversation.countUnreadMessages(),
                conversation.getCreatedAt()
        );
    }
}
