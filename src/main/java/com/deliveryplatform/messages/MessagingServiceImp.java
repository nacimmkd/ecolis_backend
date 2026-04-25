package com.deliveryplatform.messages;

import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.images.dto.ImageResponse;
import com.deliveryplatform.messages.dto.*;
import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessagingServiceImp implements MessagingService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository         userRepository;
    private final ImageService           imageService;
    private final SimpMessagingTemplate  messagingTemplate;

    private static final String WS_DEST = "/queue/messages";



    @Override
    @Transactional
    public ConversationResponse getOrCreateConversation(UUID otherUserId, UUID currentUserId) {
        var conversation = conversationRepository
                .findByParticipants(currentUserId, otherUserId)
                .orElseGet(() -> createConversation(currentUserId, otherUserId));

        return toConversationResponse(conversation, currentUserId);
    }

    @Override
    public List<ConversationResponse> getUserConversations(UUID currentUserId) {
        return conversationRepository.findAllByMemberId(currentUserId).stream()
                .map(c -> toConversationResponse(c, currentUserId))
                .toList();
    }

    @Override
    public ConversationDetailedResponse getConversationDetails(UUID conversationId, UUID currentUserId) {
        var conversation = getConversationWithMessagesOrThrow(conversationId);
        assertIsParticipant(conversation, currentUserId);

        var messages = messageRepository.getMessagesByConversationId(conversationId).stream()
                .map(this::toMessageResponse)
                .toList();

        var receiver = resolveOtherParticipant(conversation, currentUserId);
        return ConversationDetailedResponse.of(
                conversation,
                ChatUser.of(receiver, resolveAvatarUrl(receiver)),
                messages
        );
    }

    @Override
    @Transactional
    public void deleteConversation(UUID conversationId, UUID currentUserId) {
        var conversation = getConversationOrThrow(conversationId);
        assertIsParticipant(conversation, currentUserId);
        conversationRepository.delete(conversation);
    }

    @Override
    @Transactional
    public void sendMessage(SendMessageRequest request, UUID currentUserId) {
        var conversation = getConversationOrThrow(request.conversationId());
        assertIsParticipant(conversation, currentUserId);
        assertMessageNotEmpty(request);

        var sender = resolveCurrentUser(conversation, currentUserId);
        var message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.content())
                .images(resolveImages(request.imageIds()))
                .build();

        conversation.addMessage(message);
        conversationRepository.save(conversation);

        broadcastToReceiver(conversation, currentUserId, toMessageResponse(message));
    }

    // ----------------------------------------------------------------

    private Conversation createConversation(UUID currentUserId, UUID otherUserId) {
        var current = getUserByIdOrThrow(currentUserId);
        var other   = getUserByIdOrThrow(otherUserId);
        return conversationRepository.save(Conversation.builder()
                .participants(List.of(current, other))
                .build()
        );
    }

    private ConversationResponse toConversationResponse(Conversation conversation, UUID currentUserId) {
        var receiver = resolveOtherParticipant(conversation, currentUserId);
        return ConversationResponse.of(conversation, ChatUser.of(receiver, resolveAvatarUrl(receiver)));
    }

    private MessageResponse toMessageResponse(Message message) {
        var sender = message.getSender();
        var images = message.getImages().stream()
                .filter(Image::isConfirmed)
                .map(img -> imageService.getReadUrl(img.getId()))
                .toList();
        return MessageResponse.of(message, ChatUser.of(sender, resolveAvatarUrl(sender)), images);
    }

    private String resolveAvatarUrl(User user) {
        if (user.getProfile() == null) return null;
        var avatar = user.getProfile().getAvatar();
        if (avatar == null || !avatar.isConfirmed()) return null;
        return imageService.getReadUrl(avatar.getId());
    }

    private User resolveCurrentUser(Conversation conversation, UUID currentUserId) {
        return conversation.getParticipants().stream()
                .filter(m -> m.getId().equals(currentUserId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private List<Image> resolveImages(List<UUID> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) return List.of();
        return imageService.getImageEntities(imageIds);
    }

    private void assertIsParticipant(Conversation conversation, UUID userId) {
        if (!conversation.involves(userId))
            throw new UnauthorizedActionException("User is not a participant of this conversation");
    }

    private void assertMessageNotEmpty(SendMessageRequest request) {
        boolean hasContent = request.content() != null && !request.content().isBlank();
        boolean hasImages  = request.imageIds() != null && !request.imageIds().isEmpty();
        if (!hasContent && !hasImages)
            throw new InvalidDomainStateException("Message must have content or at least one image");
    }

    private void broadcastToReceiver(Conversation conversation, UUID currentUserId, MessageResponse message) {
        var receiverId = resolveOtherParticipant(conversation, currentUserId).getId();
        messagingTemplate.convertAndSendToUser(receiverId.toString(), WS_DEST, message);
    }

    private Conversation getConversationOrThrow(UUID id) {
        return conversationRepository.getConversationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
    }

    private Conversation getConversationWithMessagesOrThrow(UUID id) {
        return conversationRepository.getConversationWithMessagesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
    }

    private User getUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User resolveOtherParticipant(Conversation conversation, UUID currentUserId) {
        return conversation.getParticipants().stream()
                .filter(m -> !m.getId().equals(currentUserId))
                .findFirst()
                .orElseThrow(() -> new InvalidDomainStateException("Other participant not found"));
    }
}
