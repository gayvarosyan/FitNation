package com.example.fitnationrestapi.repository;

import com.example.fitnationrestapi.entity.ChatConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    Optional<ChatConversation> findByClientIdAndTrainerId(Long clientId, Long trainerId);

    @Query("""
        SELECT DISTINCT c FROM ChatConversation c
        LEFT JOIN FETCH c.client
        LEFT JOIN FETCH c.trainer
        WHERE c.client.id = :userId OR c.trainer.id = :userId
        ORDER BY COALESCE(c.lastMessageAt, c.createdAt) DESC
    """)
    Page<ChatConversation> findAllByUserIdWithUsers(@Param("userId") Long userId, Pageable pageable);
}
