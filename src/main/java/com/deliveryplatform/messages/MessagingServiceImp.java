package com.deliveryplatform.messages;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageRepository;
import com.deliveryplatform.images.dto.ImageResponse;
import com.deliveryplatform.messages.dto.ConversationDetailedResponse;
import com.deliveryplatform.messages.dto.ConversationResponse;
import com.deliveryplatform.messages.dto.MessageResponse;
import com.deliveryplatform.messages.dto.SendMessageRequest;
import com.deliveryplatform.storage.StorageService;
import com.deliveryplatform.users.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MessagingServiceImp implements  MessagingService {

    private final ConversationRepository    conversationRepository;
    private final BookingRepository         bookingRepository;
    private final MessageRepository         messageRepository;
    private final ImageRepository           imageRepository;
    private final StorageService            storageService;
    private final SimpMessagingTemplate     messagingTemplate;

    @Override
    @Transactional
    public ConversationResponse getOrCreateConversation(UUID bookingId, UUID currentUserId) {

        var conversation = conversationRepository.getConversationByBookingId(bookingId)
                .orElseGet(() -> {
                    var booking = getBookingOrThrow(bookingId);
                    assertIsBookingParticipant(booking, currentUserId);
                    return conversationRepository.save(
                            Conversation.builder().booking(booking).build()
                    );
                });

        assertIsBookingParticipant(conversation.getBooking(), currentUserId);
        return ConversationResponse.of(
                conversation,
                resolveReceiver(conversation.getBooking(), currentUserId)
        );
    }


    @Override
    public List<ConversationResponse> getUserConversations(UUID currentUserId) {
        return conversationRepository.findAllByUserId(currentUserId)
                .stream()
                .map(conversation ->
                        ConversationResponse.of(conversation, resolveReceiver(conversation.getBooking(), currentUserId))
                )
                .toList();
    }

    @Override
    @Transactional
    public ConversationDetailedResponse getConversationDetails(UUID conversationId, UUID currentUserId) {
        var conversation = getConversationOrThrow(conversationId);
        assertIsBookingParticipant(conversation.getBooking(), currentUserId);
        var messages = messageRepository
                .findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(this::toMessageResponse)
                .toList();

        return ConversationDetailedResponse.of(
                conversation,
                resolveReceiver(conversation.getBooking(), currentUserId),
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
    public void markConversationAsRead(UUID conversationId, UUID currentUserId) {
        messageRepository.markConversationAsRead(conversationId,currentUserId);
    }


    @Override
    @Transactional
    public void sendMessage(SendMessageRequest request, UUID currentUserId) {
        var conversation = getConversationOrThrow(request.conversationId());
        assertIsBookingParticipant(conversation.getBooking(), currentUserId);
        assertMessageNotEmpty(request);

        var message = messageRepository.save(Message.builder()
                .conversation(conversation)
                .sender(resolveSender(conversation.getBooking(), currentUserId))
                .content(request.content())
                .images(resolveImages(request.imageIds()))
                .build()
        );

        var response = toMessageResponse(message);

        broadcastToReceiver(conversation.getBooking(), currentUserId, response);
    }



    //---------------------------------------------------------------------

    private Conversation getConversationOrThrow(UUID conversationId) {
        return conversationRepository.getConversationById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + conversationId));
    }

    private Booking getBookingOrThrow(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private void assertIsBookingParticipant(Booking booking, UUID userId) {
        if (!booking.involves(userId)) {
            throw new UnauthorizedActionException(
                    "User " + userId + " is not a participant of booking: " + booking.getId()
            );
        }
    }

    private User resolveReceiver(Booking booking, UUID currentUserId) {
        return booking.getSender().getId().equals(currentUserId)
                ? booking.getCarrier()
                : booking.getSender();
    }

    private User resolveSender(Booking booking, UUID currentUserId) {
        return booking.getSender().getId().equals(currentUserId)
                ? booking.getSender()
                : booking.getCarrier();
    }

    private List<Image> resolveImages(List<UUID> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) return List.of();
        return imageRepository.findAllById(imageIds);
    }

    private void assertMessageNotEmpty(SendMessageRequest request) {
        if (request.content() == null &&
                (request.imageIds() == null || request.imageIds().isEmpty())) {
            throw new IllegalArgumentException("Message must have content or at least one image");
        }
    }

    private void broadcastToReceiver(Booking booking, UUID currentUserId, MessageResponse message) {
        var receiverId = resolveReceiver(booking, currentUserId).getId();
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/messages",
                message
        );
    }

    private MessageResponse toMessageResponse(Message message) {
        var images = message.getImages()
                .stream()
                .map(image -> ImageResponse.of(
                        image,
                        storageService.generateReadUrl(image.getKey())
                ))
                .toList();

        return MessageResponse.of(message, images);
    }

}
