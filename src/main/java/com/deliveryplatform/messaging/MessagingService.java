package com.deliveryplatform.messaging;

import com.deliveryplatform.messaging.dto.ConversationDetailedResponse;
import com.deliveryplatform.messaging.dto.ConversationResponse;
import com.deliveryplatform.messaging.dto.SendMessageRequest;

import java.util.List;
import java.util.UUID;

public interface MessagingService {
    ConversationResponse getOrCreateConversation(UUID bookingId, UUID currentUserId);
    List<ConversationResponse> getUserConversations(UUID currentUserId);
    ConversationDetailedResponse getConversationDetails(UUID conversationId, UUID currentUserId);
    void deleteConversation(UUID conversationId, UUID currentUserId);
    void markConversationAsRead(UUID conversationId, UUID currentUserId);
    void sendMessage(SendMessageRequest sendMessageRequest, UUID currentUserId);
}
