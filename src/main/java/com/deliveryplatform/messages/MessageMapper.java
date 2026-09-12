package com.deliveryplatform.messages;

import com.deliveryplatform.messages.dto.*;
import com.deliveryplatform.profiles.ProfileBriefMapper;
import com.deliveryplatform.profiles.dto.ProfileBrief;
import com.deliveryplatform.users.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ProfileBriefMapper.class, MessageImageMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MessageMapper {

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "participants", expression = "java(resolveParticipants(conversation))")
    @Mapping(target = "lastMessage", source = "messages", qualifiedByName = "resolveLastMessage")
    ConversationSummary toSummaryDto(Conversation conversation);

    @Mapping(target = "conversationId", source = "id")
    @Mapping(target = "participants", expression = "java(resolveParticipants(conversation))")
    ConversationDetails toDetailsDto(Conversation conversation);

    @Mapping(target = "messageId", source = "id")
    MessageSummary toSummaryDto(Message message);

    ProfileBrief toProfileBrief(User user);

    default List<ProfileBrief> resolveParticipants(Conversation conversation) {
        return List.of(toProfileBrief(conversation.getSender()), toProfileBrief(conversation.getCarrier()));
    }

    @Named("resolveLastMessage")
    default MessageSummary resolveLastMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return toSummaryDto(messages.get(messages.size() - 1));
    }
}