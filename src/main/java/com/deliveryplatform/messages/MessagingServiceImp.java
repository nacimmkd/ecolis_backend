package com.deliveryplatform.messages;

import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.messages.dto.*;
import com.deliveryplatform.messages.exceptions.MessageErrorCode;
import com.deliveryplatform.messages.exceptions.MessageException;
import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessagingServiceImp implements MessagingService {

    private final ConversationRepository conversationRepository;
    private final UserRepository         userRepository;
    private final ImageService           imageService;
    private final SimpMessagingTemplate  messagingTemplate;
    private final MessageMapper          messageMapper;

    private static final String WS_DEST = "/queue/messages";


    @Override
    @Transactional
    public ConversationDetails getOrCreateConversation(UUID otherUserId, UUID currentUserId) {
        var conversation = conversationRepository
                .findByParticipants(currentUserId, otherUserId)
                .orElseGet(() -> createAndSaveConversation(currentUserId, otherUserId));

        return messageMapper.toDetailsDto(conversation);
    }

    @Override
    public List<ConversationSummary> getUserConversations(UUID currentUserId) {
        return conversationRepository.findAllByMemberId(currentUserId).stream()
                .map(messageMapper::toSummaryDto)
                .toList();
    }

    @Override
    public ConversationDetails getConversationDetails(UUID conversationId, UUID currentUserId) {
        var conversation = conversationRepository.getConversationWithMessagesById(conversationId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.CONVERSATION_NOT_FOUND, "Conversation not found"));
        conversation.assertIsParticipant(currentUserId);
        return messageMapper.toDetailsDto(conversation);
    }

    @Override
    @Transactional
    public void sendMessage(SendMessageRequest request, UUID currentUserId) {
        var conversation = getConversationOrThrow(request.conversationId());
        conversation.assertIsParticipant(currentUserId);

        var sender = conversation.resolveParticipant(currentUserId);
        var images = imageService.getImages(request.imageIds());

        var message = Message.create(conversation, sender, request.content(), images);
        conversation.addMessage(message);
        conversationRepository.save(conversation);

        var receiver = conversation.resolveOtherParticipant(currentUserId);
        messagingTemplate.convertAndSendToUser(receiver.getId().toString(), WS_DEST, message);
    }

    @Override
    @Transactional
    public int markConversationAsRead(UUID conversationId, UUID currentUserId) {
        var conversation = getConversationOrThrow(conversationId);
        conversation.assertIsParticipant(currentUserId);
        return conversationRepository.markMessagesAsRead(conversationId, currentUserId, OffsetDateTime.now());
    }


    @Override
    public long getUnreadCount(UUID conversationId, UUID currentUserId) {
        var conversation = getConversationOrThrow(conversationId);
        conversation.assertIsParticipant(currentUserId);

        return conversationRepository.countUnreadMessages(conversationId, currentUserId);
    }


    // Private ---------------------------------------------------------------------------

    private Conversation createAndSaveConversation(UUID currentUserId, UUID otherUserId) {
        var current = getUserOrThrow(currentUserId);
        var other   = getUserOrThrow(otherUserId);
        return conversationRepository.save(
                Conversation.create(List.of(current, other))
        );
    }

    private Conversation getConversationOrThrow(UUID id) {
        return conversationRepository.getConversationById(id)
                .orElseThrow(() -> new MessageException(MessageErrorCode.CONVERSATION_NOT_FOUND, "Conversation not found"));
    }


    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new MessageException(MessageErrorCode.PARTICIPANT_NOT_FOUND, "Conversation participant not found"));
    }
}