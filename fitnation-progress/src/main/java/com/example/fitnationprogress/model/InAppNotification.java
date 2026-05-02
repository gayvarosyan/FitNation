package com.example.fitnationprogress.model;

import com.example.fitnationprogress.enums.NotificationEntityType;
import com.example.fitnationprogress.enums.NotificationEventType;
import com.example.fitnationprogress.enums.NotificationSeverity;
import com.example.fitnationuser.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "in_app_notifications", schema = "public")
public class InAppNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_user_id", nullable = false, referencedColumnName = "id")
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 80)
    private NotificationEventType eventType;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 4000)
    private String body;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "metadata_json", nullable = false, length = 8000)
    private String metadataJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 80)
    private NotificationEntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    public void markRead() {
        this.read = true;
    }
}
