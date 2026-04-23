package com.deliveryplatform.messages;

import com.deliveryplatform.bookings.Booking;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @OrderBy("sentAt ASC")
    private List<Message> messages;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt =  OffsetDateTime.now();


    public Message getLastMessage(){
        if (messages == null || messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public void addMessage(Message message){
        message.setConversation(this);
        messages.add(message);
    }
}
