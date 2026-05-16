package com.deliveryplatform.messages;

import com.deliveryplatform.images.ImageMapper;
import com.deliveryplatform.messages.dto.*;
import com.deliveryplatform.users.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final UserMapper userMapper;
    private final ImageMapper imageMapper;

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
                .sender(userMapper.toRefDto(message.getSender()))
                .content(message.getContent())
                .images(imageMapper.toDto(message.getImages()))
                .sentAt(message.getSentAt())
                .build();
    }

    // Helpers ─────────────────────────────────────────────

    private MessageSummary resolveLastMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) return null;
        return toSummaryDto(messages.get(messages.size() - 1));
    }
}