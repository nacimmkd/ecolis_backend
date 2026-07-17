package com.deliveryplatform.messages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {


    @Query("SELECT c FROM Conversation c JOIN c.participants m1 JOIN c.participants m2 WHERE m1.id = :userId1 AND m2.id = :userId2")
    Optional<Conversation> findByParticipants(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    @Query("SELECT c FROM Conversation c JOIN c.participants m WHERE m.id = :userId")
    List<Conversation> findAllByMemberId(@Param("userId") UUID userId);

    @Query("SELECT c FROM Conversation c WHERE c.id = :id")
    Optional<Conversation> getConversationById(@Param("id") UUID id);

    @Query("SELECT c FROM Conversation c LEFT JOIN FETCH c.messages m LEFT JOIN FETCH m.sender WHERE c.id = :id")
    Optional<Conversation> getConversationWithMessagesById(@Param("id") UUID id);

    @Modifying
    @Query("""
        UPDATE Message m
        SET m.read = true, m.readAt = :now
        WHERE m.conversation.id = :conversationId
          AND m.sender.id <> :readerId
          AND m.read = false
        """)
    int markMessagesAsRead(
            @Param("conversationId") UUID conversationId,
            @Param("readerId") UUID readerId,
            @Param("now") OffsetDateTime now
    );

    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.conversation.id = :conversationId
          AND m.sender.id <> :userId
          AND m.read = false
        """)
    long countUnreadMessages(
            @Param("conversationId") UUID conversationId,
            @Param("userId") UUID userId
    );

    @Query("""
    SELECT m FROM Message m
    JOIN FETCH m.sender
    JOIN FETCH m.conversation c
    JOIN FETCH c.participants
    WHERE m.read = false
      AND m.notified = false
      AND m.sentAt <= :threshold
    ORDER BY m.conversation.id, m.sentAt ASC
    """)
    List<Message> findUnreadUnnotifiedMessages(@Param("threshold") OffsetDateTime threshold);
}
