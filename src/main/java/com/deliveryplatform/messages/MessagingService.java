package com.deliveryplatform.messages;

import com.deliveryplatform.messages.dto.ConversationDetailedResponse;
import com.deliveryplatform.messages.dto.ConversationResponse;
import com.deliveryplatform.messages.dto.SendMessageRequest;

import java.util.List;
import java.util.UUID;

public interface MessagingService {
    ConversationResponse getOrCreateConversation(UUID otherUserId, UUID currentUserId);
    List<ConversationResponse> getUserConversations(UUID currentUserId);
    ConversationDetailedResponse getConversationDetails(UUID conversationId, UUID currentUserId);
    void deleteConversation(UUID conversationId, UUID currentUserId);
    void sendMessage(SendMessageRequest request, UUID currentUserId);
}
