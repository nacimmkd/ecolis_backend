package com.deliveryplatform.messages;

import com.deliveryplatform.messages.dto.ConversationDetails;
import com.deliveryplatform.messages.dto.ConversationSummary;
import com.deliveryplatform.messages.dto.SendMessageRequest;

import java.util.List;
import java.util.UUID;

public interface MessagingService {
    ConversationDetails getOrCreateConversation(UUID otherUserId, UUID currentUserId);
    List<ConversationSummary> getUserConversations(UUID currentUserId);
    ConversationDetails getConversationDetails(UUID conversationId, UUID currentUserId);
    void sendMessage(SendMessageRequest request, UUID currentUserId);
    int markConversationAsRead(UUID conversationId, UUID readerId);
    long getUnreadCount(UUID conversationId, UUID userId);
}
