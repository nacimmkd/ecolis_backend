package com.deliveryplatform.messages;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.messages.dto.ConversationDetailedResponse;
import com.deliveryplatform.messages.dto.ConversationResponse;
import com.deliveryplatform.messages.dto.MessageResponse;
import com.deliveryplatform.profiles.ProfileResolver;
import com.deliveryplatform.profiles.dto.ProfileSummary;
import com.deliveryplatform.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageResolver {

    private final MessageMapper   messageMapper;
    private final ImageService imageService;
    private final ProfileResolver profileResolver;

    public ConversationResponse resolveSummary(Conversation conversation) {
        return messageMapper.toResponse(conversation)
                .withParticipants(resolveParticipants(conversation.getParticipants()))
                .withLastMessage(conversation.getLastMessage() != null
                        ? conversation.getLastMessage().getContent()
                        : null);
    }

    public ConversationDetailedResponse resolveDetailed(Conversation conversation) {
        var messages = conversation.getMessages().stream()
                .map(this::resolveMessage)
                .toList();

        return messageMapper.toDetailedResponse(conversation)
                .withParticipants(resolveParticipants(conversation.getParticipants()))
                .withMessages(messages);
    }

    public MessageResponse resolveMessage(Message message) {
        return messageMapper.toMessageResponse(message)
                .withSender(profileResolver.resolveSummary(message.getSender().getProfile()))
                .withImagesUrls(resolveImageUrls(message.getImages()));
    }

    // ----------------------------------------------------------------

    private List<ProfileSummary> resolveParticipants(List<User> participants) {
        return participants.stream()
                .map(u -> profileResolver.resolveSummary(u.getProfile()))
                .toList();
    }

    private List<String> resolveImageUrls(List<Image> images) {
        return images.stream()
                .map(image -> imageService.getReadUrl(image.getId()))
                .toList();
    }
}