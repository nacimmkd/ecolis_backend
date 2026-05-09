package com.deliveryplatform.messages;

import com.deliveryplatform.messages.dto.*;
import com.deliveryplatform.profiles.ProfileMapper;
import org.mapstruct.DecoratedWith;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = ProfileMapper.class
)
@DecoratedWith(MessageMapperDecorator.class)
public interface MessageMapper {

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "lastMessage", ignore = true)
    ConversationSummary toSummaryDto(Conversation conversation);

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "messages", ignore = true)
    ConversationDetails toDetailsDto(Conversation conversation);

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "imagesUrls", ignore = true)
    MessageSummary toSummaryDto(Message message);
}