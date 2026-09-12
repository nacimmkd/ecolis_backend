package com.deliveryplatform.messages;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.messages.exceptions.MessageErrorCode;
import com.deliveryplatform.messages.exceptions.MessageException;
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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id", nullable = false)
    private User carrier;


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


    public static Conversation createFromBooking(Booking booking) {
        return Conversation.builder()
                .booking(booking)
                .sender(booking.getSender())
                .carrier(booking.getCarrier())
                .lastMessage(null)
                .build();
    }

    public void addMessage(Message message) {
        messages.add(message);
        lastMessage = message;
    }

    public boolean involves(UUID userId) {
        return sender.getId().equals(userId) || carrier.getId().equals(userId);
    }


    public User resolveParticipant(UUID userId) {
        if (sender.getId().equals(userId)) return sender;
        if (carrier.getId().equals(userId)) return carrier;
        throw new MessageException(MessageErrorCode.PARTICIPANT_NOT_FOUND, "Conversation participant not found");
    }

    public User resolveOtherParticipant(UUID userId) {
        if (sender.getId().equals(userId)) return carrier;
        if (carrier.getId().equals(userId)) return sender;
        throw new MessageException(MessageErrorCode.PARTICIPANT_NOT_FOUND, "Other conversation participant not found");
    }

    // --------- assertions -------------------------------------------------------------------------------

    public void assertIsParticipant(UUID userId) {
        if (!this.involves(userId))
            throw new MessageException(MessageErrorCode.NOT_A_PARTICIPANT, "User is not a participant of this conversation");
    }
}