package com.deliveryplatform.messages;

import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.messages.dto.*;
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
    private final MessageMapper        messageMapper;

    private static final String WS_DEST = "/queue/messages";


    @Override
    @Transactional
    public ConversationDetails getOrCreateConversation(UUID otherUserId, UUID currentUserId) {
        var conversation = conversationRepository
                .findByParticipants(currentUserId, otherUserId)
                .orElseGet(() -> createConversation(currentUserId, otherUserId));

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
        var conversation = getConversationWithMessagesOrThrow(conversationId);
        assertUserIsParticipant(conversation, currentUserId);
        return messageMapper.toDetailsDto(conversation);
    }

    @Override
    @Transactional
    public void sendMessage(SendMessageRequest request, UUID currentUserId) {
        var conversation = getConversationOrThrow(request.conversationId());
        assertUserIsParticipant(conversation, currentUserId);
        assertMessageNotEmpty(request);

        var sender  = resolveParticipant(conversation, currentUserId);
        var images = resolveImages(request.imageIds());

        var message = Message.create(conversation, sender, request.content(), images);
        conversation.addMessage(message);
        conversationRepository.save(conversation);

        broadcastToReceiver(conversation, currentUserId, messageMapper.toSummaryDto(message));
    }

    @Override
    @Transactional
    public int markConversationAsRead(UUID conversationId, UUID readerId) {
        var conversation = getConversationOrThrow(conversationId);
        assertUserIsParticipant(conversation, readerId);
        return conversationRepository.markMessagesAsRead(conversationId, readerId, OffsetDateTime.now());
    }


    @Override
    public long getUnreadCount(UUID conversationId, UUID userId) {
        var conversation = getConversationOrThrow(conversationId);
        assertUserIsParticipant(conversation, userId);

        return conversationRepository.countUnreadMessages(conversationId, userId);
    }


    // Private ---------------------------------------------------------------------------

    private Conversation createConversation(UUID currentUserId, UUID otherUserId) {
        var current = getUserOrThrow(currentUserId);
        var other   = getUserOrThrow(otherUserId);
        return conversationRepository.save(
                Conversation.create(List.of(current, other))
        );
    }

    private void broadcastToReceiver(Conversation conversation, UUID currentUserId, MessageSummary message) {
        var receiverId = resolveOtherParticipant(conversation, currentUserId).getId();
        messagingTemplate.convertAndSendToUser(receiverId.toString(), WS_DEST, message);
    }

    private List<Image> resolveImages(List<UUID> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) return List.of();
        return imageService.getImages(imageIds);
    }


    private void assertUserIsParticipant(Conversation conversation, UUID userId) {
        if (!conversation.involves(userId))
            throw new UnauthorizedActionException("User is not a participant of this conversation");
    }

    private void assertMessageNotEmpty(SendMessageRequest request) {
        boolean hasContent = request.content() != null && !request.content().isBlank();
        boolean hasImages  = request.imageIds() != null && !request.imageIds().isEmpty();
        if (!hasContent && !hasImages)
            throw new InvalidDomainStateException("Message must have content or at least one image");
    }

    private User resolveParticipant(Conversation conversation, UUID userId) {
        return conversation.getParticipants().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User not found in conversation"));
    }

    private User resolveOtherParticipant(Conversation conversation, UUID currentUserId) {
        return conversation.getParticipants().stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .findFirst()
                .orElseThrow(() -> new InvalidDomainStateException("Other participant not found"));
    }

    private Conversation getConversationOrThrow(UUID id) {
        return conversationRepository.getConversationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
    }

    private Conversation getConversationWithMessagesOrThrow(UUID id) {
        return conversationRepository.getConversationWithMessagesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}