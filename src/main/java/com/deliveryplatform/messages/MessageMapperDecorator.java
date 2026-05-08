package com.deliveryplatform.messages;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.messages.dto.ConversationDetails;
import com.deliveryplatform.messages.dto.ConversationSummary;
import com.deliveryplatform.messages.dto.MessageSummary;
import com.deliveryplatform.profiles.ProfileMapper;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import com.deliveryplatform.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class MessageMapperDecorator implements MessageMapper {

    @Autowired
    private MessageMapper delegate;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public ConversationSummary toSummaryDto(Conversation conversation) {

        ConversationSummary dto =
                delegate.toSummaryDto(conversation);

        return dto.toBuilder()
                .participants(
                        resolveParticipants(
                                conversation.getParticipants()
                        )
                )
                .lastMessage(
                        conversation.getLastMessage() != null
                                ? conversation.getLastMessage().getContent()
                                : null
                )
                .build();
    }

    @Override
    public ConversationDetails toDetailsDto(Conversation conversation) {

        ConversationDetails dto =
                delegate.toDetailsDto(conversation);

        return dto.toBuilder()
                .participants(
                        resolveParticipants(
                                conversation.getParticipants()
                        )
                )
                .messages(
                        conversation.getMessages()
                                .stream()
                                .map(this::toSummaryDto)
                                .toList()
                )
                .build();
    }

    @Override
    public MessageSummary toSummaryDto(Message message) {

        MessageSummary dto =
                delegate.toSummaryDto(message);

        return dto.toBuilder()
                .sender(
                        profileMapper.toSummaryDto(
                                message.getSender().getProfile()
                        )
                )
                .imagesUrls(
                        resolveImageUrls(message.getImages())
                )
                .build();
    }

    // Helpers ─────────────────────────────────────────────

    private List<ProfileSummary> resolveParticipants(
            List<User> participants
    ) {

        if (participants == null || participants.isEmpty()) {
            return List.of();
        }

        return participants.stream()
                .map(user ->
                        profileMapper.toSummaryDto(
                                user.getProfile()
                        )
                )
                .toList();
    }

    private List<String> resolveImageUrls(List<Image> images) {

        if (images == null || images.isEmpty()) {
            return List.of();
        }

        return images.stream()
                .map(image ->
                        imageService.getReadUrl(
                                image.getId()
                        )
                )
                .toList();
    }
}