package com.deliveryplatform.messages;

import com.deliveryplatform.messages.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "participants",       ignore = true)
    @Mapping(target = "lastMessage",    ignore = true)
    @Mapping(target = "createdAt",      source = "createdAt")
    ConversationResponse toResponse(Conversation conversation);

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "participants",       ignore = true)
    @Mapping(target = "messages",       ignore = true)
    @Mapping(target = "createdAt",      source = "createdAt")
    ConversationDetailedResponse toDetailedResponse(Conversation conversation);

    @Mapping(target = "messageId",  source = "id")
    @Mapping(target = "sender",     ignore = true)
    @Mapping(target = "content",    source = "content")
    @Mapping(target = "imagesUrls", ignore = true)
    @Mapping(target = "sentAt",     source = "sentAt")
    MessageResponse toMessageResponse(Message message);
}