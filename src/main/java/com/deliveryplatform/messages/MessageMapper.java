package com.deliveryplatform.messages;

import com.deliveryplatform.messages.dto.*;
import org.mapstruct.DecoratedWith;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@DecoratedWith(MessageMapperDecorator.class)
public interface MessageMapper {

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "lastMessage", ignore = true)
    ConversationSummary toSummaryDto(Conversation conversation);

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "messages", ignore = true)
    ConversationDetails toDetailsDto(Conversation conversation);

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "imagesUrls", ignore = true)
    MessageSummary toSummaryDto(Message message);
}