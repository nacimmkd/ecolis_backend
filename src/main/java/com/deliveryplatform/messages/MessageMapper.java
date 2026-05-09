package com.deliveryplatform.messages;

import com.deliveryplatform.messages.dto.*;
import com.deliveryplatform.profiles.ProfileMapper;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = ProfileMapper.class
)
@DecoratedWith(MessageMapperDecorator.class)
public interface MessageMapper {

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "lastMessage", source = "messages", qualifiedByName = "mapLastMessage")
    ConversationSummary toSummaryDto(Conversation conversation);

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "messages", ignore = true)
    ConversationDetails toDetailsDto(Conversation conversation);

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "imagesUrls", ignore = true)
    MessageSummary toSummaryDto(Message message);


    @Named("mapLastMessage")
    default MessageSummary mapLastMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        Message lastMessage = messages.get(messages.size() - 1);
        return toSummaryDto(lastMessage);
    }
}