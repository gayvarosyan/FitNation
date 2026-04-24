package com.example.fitnationrestapi.repository;

import com.example.fitnationrestapi.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

    @Query("""
        SELECT m FROM ChatMessage m
        LEFT JOIN FETCH m.sender
        WHERE m.conversation.id = :conversationId
        ORDER BY m.createdAt DESC
    """)
    Page<ChatMessage> findByConversationIdOrderByCreatedAtDescWithSender(@Param("conversationId") Long conversationId, Pageable pageable);
}
