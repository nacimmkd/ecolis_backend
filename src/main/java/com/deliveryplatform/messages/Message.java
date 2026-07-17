package com.deliveryplatform.messages;


import com.deliveryplatform.images.Image;
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

    @ManyToMany
    @JoinTable(
            name = "message_images",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    @Builder.Default
    private List<Image> images = new ArrayList<>();

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
            List<Image> images
    ) {
        return Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(content)
                .images(images)
                .build();
    }


    public void markAsNotified() {
        this.notified = true;
    }

}