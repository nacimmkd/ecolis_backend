package com.deliveryplatform.messages;

import com.deliveryplatform.messages.events.NewMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnreadMessageNotificationScheduler {

    private static final Duration UNREAD_DELAY = Duration.ofHours(1);

    private final ConversationRepository conversationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 60 * 60 * 1000) // 1h
    @Transactional
    public void notifyUnreadMessages() {
        var threshold = OffsetDateTime.now().minus(UNREAD_DELAY);
        var messages = conversationRepository.findUnreadUnnotifiedMessages(threshold);

        if (messages.isEmpty()) return;

        var groupedByConversation = messages.stream()
                .collect(Collectors.groupingBy(m -> m.getConversation().getId()));

        for (var entry : groupedByConversation.entrySet()) {
            notifyConversationUnreadMessages(entry.getValue());
        }
    }

    private void notifyConversationUnreadMessages(List<Message> conversationMessages) {
        var lastMessage = conversationMessages.get(conversationMessages.size() - 1);
        var sender = lastMessage.getSender();
        var receiver = lastMessage.getConversation().resolveOtherParticipant(sender.getId());

        eventPublisher.publishEvent(new NewMessageEvent(
                lastMessage.getConversation().getId(),
                receiver,
                sender.getProfile().getFirstName(),
                conversationMessages.size()
        ));

        conversationMessages.forEach(Message::markAsNotified);
    }
}