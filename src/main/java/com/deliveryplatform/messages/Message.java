package com.deliveryplatform.messages;


import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MessageImage> images = new ArrayList<>();

    @Column(name = "sent_at")
    @Builder.Default
    private OffsetDateTime sentAt = OffsetDateTime.now();

    @Column(name = "read")
    @Builder.Default
    private boolean read = false;

    @Column(name = "read_at")
    @Builder.Default
    private OffsetDateTime readAt = null;

    @Column(name = "notified")
    @Builder.Default
    private boolean notified = false;


    public static Message create(
            Conversation conversation,
            User sender,
            String content,
            List<MessageImage> images
    ) {
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(content)
                .build();

        images.forEach(message::addImage);

        return message;
    }

    private void addImage(MessageImage image) {
        image.setMessage(this);
        this.images.add(image);
    }


    public void markAsNotified() {
        this.notified = true;
    }

}