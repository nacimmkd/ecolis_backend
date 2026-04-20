package com.deliveryplatform.messages.dto;


import com.deliveryplatform.messages.Conversation;
import com.deliveryplatform.users.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ConversationDetailedResponse(
        UUID id,
        UUID            bookingId,
        ChatUser        receiver,
        List<MessageResponse> messages,
        OffsetDateTime createdAt
) {


    public static ConversationDetailedResponse of(Conversation conversation, User receiver, List<MessageResponse> messages) {
        return new ConversationDetailedResponse(
                conversation.getId(),
                conversation.getBooking().getId(),
                ChatUser.of(receiver),
                messages,
                conversation.getCreatedAt()
        );
    }
}
