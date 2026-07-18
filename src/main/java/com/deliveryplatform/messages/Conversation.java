package com.deliveryplatform.messages;

import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "conversations_participants",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<User> participants;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;


    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("sentAt ASC")
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();


    public static Conversation create(List<User> participants) {

        if (participants.size() != 2)
            throw new IllegalArgumentException("conversation must have 2 participants");

        return Conversation.builder()
                .lastMessage(null)
                .participants(participants)
                .build();
    }

    public void addMessage(Message message) {
        messages.add(message);
        lastMessage = message;
    }

    public boolean involves(UUID userId) {
        return participants.stream().anyMatch(m -> m.getId().equals(userId));
    }

    public User getReceiver(UUID senderId) {
        return participants.stream()
                .filter(p -> !p.getId().equals(senderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Conversation has no other participant"));
    }
}
