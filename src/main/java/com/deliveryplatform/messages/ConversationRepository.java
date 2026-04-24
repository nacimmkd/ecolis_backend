package com.deliveryplatform.messages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
