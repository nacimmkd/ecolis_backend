package com.deliveryplatform.messages;

import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.messages.dto.ConversationDetailedResponse;
import com.deliveryplatform.messages.dto.ConversationResponse;
import com.deliveryplatform.messages.dto.MessageResponse;
import com.deliveryplatform.profiles.ProfileResolver;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import com.deliveryplatform.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageResolver {

    private final MessageMapper   messageMapper;
    private final ImageService imageService;
    private final ProfileResolver profileResolver;

    public ConversationResponse resolveSummary(Conversation conversation, User currentUser) {
        return messageMapper.toResponse(conversation)
                .withReceiver(resolveReceiver(conversation, currentUser))
                .withLastMessage(conversation.getLastMessage() != null
                        ? conversation.getLastMessage().getContent()
                        : null);
    }

    public ConversationDetailedResponse resolveDetailed(Conversation conversation, User currentUser) {
        var messages = conversation.getMessages().stream()
                .map(this::resolveMessage)
                .toList();

        return messageMapper.toDetailedResponse(conversation)
                .withReceiver(resolveReceiver(conversation, currentUser))
                .withMessages(messages);
    }

    public MessageResponse resolveMessage(Message message) {
        var imageUrls = message.getImages().stream()
                .map(img -> imageService.getReadUrl(img.getId()))
                .toList();

        return messageMapper.toMessageResponse(message)
                .withSender(profileResolver.resolveSummary(message.getSender().getProfile()))
                .withImagesUrls(imageUrls);
    }

    // ----------------------------------------------------------------

    private ProfileSummary resolveReceiver(Conversation conversation, User currentUser) {
        return conversation.getParticipants().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .findFirst()
                .map(u -> profileResolver.resolveSummary(u.getProfile()))
                .orElseThrow(() -> new IllegalStateException("Receiver not found"));
    }
}