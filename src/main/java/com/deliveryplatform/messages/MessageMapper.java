package com.deliveryplatform.messages;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.messages.dto.*;
import com.deliveryplatform.users.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final UserMapper userMapper;
    private final ImageService imageService;

    public ConversationSummary toSummaryDto(Conversation conversation) {
        return ConversationSummary.builder()
                .conversationId(conversation.getId())
                .participants(conversation.getParticipants().stream()
                        .map(userMapper::toSummaryDto)
                        .toList())
                .lastMessage(resolveLastMessage(conversation.getMessages()))
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    public ConversationDetails toDetailsDto(Conversation conversation) {
        return ConversationDetails.builder()
                .conversationId(conversation.getId())
                .participants(conversation.getParticipants().stream()
                        .map(userMapper::toSummaryDto)
                        .toList())
                .messages(conversation.getMessages().stream()
                        .map(this::toSummaryDto)
                        .toList())
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    public MessageSummary toSummaryDto(Message message) {
        return MessageSummary.builder()
                .messageId(message.getId())
                .sender(userMapper.toSummaryDto(message.getSender()))
                .content(message.getContent())
                .imagesUrls(resolveImageUrls(message.getImages()))
                .sentAt(message.getSentAt())
                .build();
    }

    // Helpers ─────────────────────────────────────────────

    private MessageSummary resolveLastMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) return null;
        return toSummaryDto(messages.get(messages.size() - 1));
    }

    private List<String> resolveImageUrls(List<Image> images) {
        if (images == null || images.isEmpty()) return List.of();
        return images.stream()
                .map(image -> imageService.getReadUrl(image.getId()))
                .toList();
    }
}