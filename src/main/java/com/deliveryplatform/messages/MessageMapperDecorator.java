package com.deliveryplatform.messages;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.messages.dto.ConversationDetails;
import com.deliveryplatform.messages.dto.ConversationSummary;
import com.deliveryplatform.messages.dto.MessageSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class MessageMapperDecorator implements MessageMapper {

    @Autowired
    private MessageMapper delegate;

    @Autowired
    private ImageService imageService;

    @Override
    public ConversationDetails toDetailsDto(Conversation conversation) {
        ConversationDetails dto = delegate.toDetailsDto(conversation);
        return dto.toBuilder()
                .messages(
                        conversation.getMessages()
                                .stream()
                                .map(this::toSummaryDto)
                                .toList()
                )
                .build();
    }

    @Override
    public MessageSummary toSummaryDto(Message message) {
        MessageSummary dto = delegate.toSummaryDto(message);
        return dto.toBuilder()
                .imagesUrls(resolveImageUrls(message.getImages()))
                .build();
    }

    // Helpers ─────────────────────────────────────────────


    private List<String> resolveImageUrls(List<Image> images) {

        if (images == null || images.isEmpty()) {
            return List.of();
        }

        return images.stream()
                .map(image ->
                        imageService.getReadUrl(
                                image.getId()
                        )
                )
                .toList();
    }
}