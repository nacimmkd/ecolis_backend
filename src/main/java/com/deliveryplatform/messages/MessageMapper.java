package com.deliveryplatform.messages;

import com.deliveryplatform.messages.dto.*;
import com.deliveryplatform.users.UserBriefMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {UserBriefMapper.class, MessageImageMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MessageMapper {

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "lastMessage", source = "messages", qualifiedByName = "resolveLastMessage")
    ConversationSummary toSummaryDto(Conversation conversation);

    @Mapping(target = "conversationId", source = "id")
    ConversationDetails toDetailsDto(Conversation conversation);

    @Mapping(target = "messageId", source = "id")
    MessageSummary toSummaryDto(Message message);

    @Named("resolveLastMessage")
    default MessageSummary resolveLastMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return toSummaryDto(messages.get(messages.size() - 1));
    }
}