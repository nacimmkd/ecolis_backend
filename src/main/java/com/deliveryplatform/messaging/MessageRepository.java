package com.deliveryplatform.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("""
          SELECT m FROM Message m
          JOIN FETCH m.images
          JOIN FETCH m.sender
          where m.id = :messageId
    """)
    Optional<Message> getMessageById(@Param("messageId") UUID messageId);

    @Query("""
          SELECT m FROM Message m
          LEFT JOIN FETCH m.images
          JOIN FETCH m.sender
          WHERE m.conversation.id = :conversationId
          ORDER BY m.sentAt ASC
    """)
    List<Message> findByConversationIdOrderBySentAtAsc(@Param("conversationId") UUID conversationId);


    @Modifying
    @Query("""
    update Message m
    set m.isRead = true
    where m.conversation.id = :conversationId
    and m.sender.id <> :userId
    and m.isRead = false
""")
    void markConversationAsRead(UUID conversationId, UUID userId);
}
