package com.deliveryplatform.messaging;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
        SELECT c FROM Conversation c
        JOIN FETCH c.booking b
        JOIN FETCH b.sender s
        JOIN FETCH b.carrier ca
        LEFT JOIN FETCH s.profile
        LEFT JOIN FETCH ca.profile
        WHERE c.id  = :conversationId
        """)
    Optional<Conversation> getConversationById(@Param("conversationId") UUID conversationId);

    @Query("""
        SELECT c FROM Conversation c
        JOIN FETCH c.booking b
        JOIN FETCH b.sender s
        JOIN FETCH b.carrier ca
        LEFT JOIN FETCH s.profile
        LEFT JOIN FETCH ca.profile
        JOIN FETCH c.messages
        WHERE b.id  = :bookingId
    """)
    Optional<Conversation> getConversationByBookingId(@Param("bookingId") UUID bookingId);

    @Query("""
        SELECT c FROM Conversation c
        JOIN FETCH c.booking b
        JOIN FETCH b.sender s
        JOIN FETCH b.carrier ca
        LEFT JOIN FETCH s.profile
        LEFT JOIN FETCH ca.profile
        JOIN FETCH c.messages
        WHERE b.sender.id  = :userId
        OR    b.carrier.id = :userId
        ORDER BY c.createdAt DESC
    """)
    List<Conversation> findAllByUserId(@Param("userId") UUID userId);

}
