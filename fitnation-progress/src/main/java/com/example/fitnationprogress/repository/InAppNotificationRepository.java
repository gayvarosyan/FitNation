package com.example.fitnationprogress.repository;

import com.example.fitnationprogress.enums.NotificationEntityType;
import com.example.fitnationprogress.enums.NotificationEventType;
import com.example.fitnationprogress.model.InAppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface InAppNotificationRepository extends JpaRepository<InAppNotification, Long> {

    @Query("""
            SELECT COUNT(n) FROM InAppNotification n
            WHERE n.recipient.id = :recipientId
              AND n.eventType = :eventType
              AND n.entityType = :entityType
              AND n.entityId = :entityId
              AND n.createdAt >= :since
            """)
    long countByRecipientAndEventAndEntitySince(
            @Param("recipientId") Long recipientId,
            @Param("eventType") NotificationEventType eventType,
            @Param("entityType") NotificationEntityType entityType,
            @Param("entityId") Long entityId,
            @Param("since") Instant since);

    @Query("""
            SELECT COUNT(n) FROM InAppNotification n
            WHERE n.recipient.id = :recipientId
              AND n.createdAt >= :since
            """)
    long countByRecipientSince(@Param("recipientId") Long recipientId, @Param("since") Instant since);

    @EntityGraph(attributePaths = "recipient")
    Page<InAppNotification> findByRecipient_IdOrderByCreatedAtDesc(Long recipientUserId, Pageable pageable);

    Optional<InAppNotification> findByIdAndRecipient_Id(Long id, Long recipientUserId);
}
