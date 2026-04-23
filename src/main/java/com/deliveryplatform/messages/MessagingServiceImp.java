package com.deliveryplatform.messages;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageRepository;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.images.dto.ImageResponse;
import com.deliveryplatform.messages.dto.*;
import com.deliveryplatform.users.User;
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
    private final BookingRepository bookingRepository;

    private final SimpMessagingTemplate messagingTemplate;
    private final ImageService imageService;

    @Override
    @Transactional
    public ConversationResponse getOrCreateConversation(UUID bookingId, UUID currentUserId) {
        var conversation = conversationRepository.getConversationByBookingId(bookingId)
                .orElseGet(() -> createConversation(bookingId, currentUserId));

        assertIsBookingParticipant(conversation.getBooking(), currentUserId);
        return toConversationResponse(conversation, currentUserId);
    }

    @Override
    public List<ConversationResponse> getUserConversations(UUID currentUserId) {
        return conversationRepository.findAllByUserId(currentUserId)
                .stream()
                .map(conversation -> toConversationResponse(conversation, currentUserId))
                .toList();
    }

    @Override
    @Transactional
    public ConversationDetailedResponse getConversationDetails(UUID conversationId, UUID currentUserId) {
        var conversation = getConversationWithMessagesOrThrow(conversationId);
        assertIsBookingParticipant(conversation.getBooking(), currentUserId);

        var messages = conversation.getMessages().stream()
                .map(this::toMessageResponse)
                .toList();

        var receiver = resolveOtherParticipant(conversation.getBooking(), currentUserId);
        var avatarUrl = resolveAvatarUrl(receiver);

        return ConversationDetailedResponse.of(
                conversation,
                ChatUser.of(receiver, avatarUrl),
                messages
        );
    }

    @Override
    @Transactional
    public void deleteConversation(UUID conversationId, UUID currentUserId) {
        var conversation = getConversationOrThrow(conversationId);
        assertIsBookingParticipant(conversation.getBooking(), currentUserId);
        conversationRepository.delete(conversation);
    }

    @Override
    @Transactional
    public void sendMessage(SendMessageRequest request, UUID currentUserId) {
        var conversation = getConversationOrThrow(request.conversationId());
        assertIsBookingParticipant(conversation.getBooking(), currentUserId);
        assertMessageNotEmpty(request);

        var sender = resolveParticipant(conversation.getBooking(), currentUserId);
        var images = resolveImages(request.imageIds());

        var message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.content())
                .images(images)
                .build();

        conversation.addMessage(message);
        conversationRepository.save(conversation);

        broadcastToReceiver(
                conversation.getBooking(),
                currentUserId,
                toMessageResponse(message)
        );
    }

    // ----------------------------------------------------------------

    private Conversation createConversation(UUID bookingId, UUID currentUserId) {
        var booking = getBookingOrThrow(bookingId);
        assertIsBookingParticipant(booking, currentUserId);
        return conversationRepository.save(Conversation.builder().booking(booking).build());
    }

    private ConversationResponse toConversationResponse(Conversation conversation, UUID currentUserId) {
        var receiver = resolveOtherParticipant(conversation.getBooking(), currentUserId);
        var avatarUrl = resolveAvatarUrl(receiver);
        return ConversationResponse.of(conversation, ChatUser.of(receiver, avatarUrl));
    }

    private MessageResponse toMessageResponse(Message message) {
        var images = message.getImages().stream()
                .filter(Image::isConfirmed)
                .map(image -> ImageResponse.of(image, imageService.getReadUrl(image.getId())))
                .toList();

        var sender = message.getSender();
        var avatarUrl = resolveAvatarUrl(sender);
        return MessageResponse.of(message, ChatUser.of(sender, avatarUrl), images);
    }

    private String resolveAvatarUrl(User user) {
        if (user.getProfile() == null) return null;
        var avatar = user.getProfile().getAvatar();
        if (avatar == null || !avatar.isConfirmed()) return null;
        return imageService.getReadUrl(avatar.getId());
    }

    private User resolveOtherParticipant(Booking booking, UUID currentUserId) {
        return booking.getSender().getId().equals(currentUserId)
                ? booking.getCarrier()
                : booking.getSender();
    }

    private User resolveParticipant(Booking booking, UUID currentUserId) {
        return booking.getSender().getId().equals(currentUserId)
                ? booking.getSender()
                : booking.getCarrier();
    }

    private List<Image> resolveImages(List<UUID> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) return List.of();
        return imageService.getImageEntities(imageIds);
    }

    private void assertIsBookingParticipant(Booking booking, UUID userId) {
        if (!booking.involves(userId)) {
            throw new UnauthorizedActionException("User is not a participant of this booking");
        }
    }

    private void assertMessageNotEmpty(SendMessageRequest request) {
        boolean hasContent = request.content() != null && !request.content().isBlank();
        boolean hasImages = request.imageIds() != null && !request.imageIds().isEmpty();
        if (!hasContent && !hasImages) {
            throw new IllegalArgumentException("Message must have content or at least one image");
        }
    }

    private void broadcastToReceiver(Booking booking, UUID currentUserId, MessageResponse message) {
        var receiverId = resolveOtherParticipant(booking, currentUserId).getId();
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/messages",
                message
        );
    }

    private Conversation getConversationOrThrow(UUID id) {
        return conversationRepository.getConversationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
    }

    private Conversation getConversationWithMessagesOrThrow(UUID id) {
        return conversationRepository.getConversationWithMessagesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
    }

    private Booking getBookingOrThrow(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }
}