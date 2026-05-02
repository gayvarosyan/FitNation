package com.example.fitnationprogress.model;

import com.example.fitnationprogress.dto.UpsertUserProgressEntryRequest;
import com.example.fitnationuser.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_progress_entries")
public class UserProgressEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(name = "body_fat_percent", precision = 5, scale = 2)
    private BigDecimal bodyFatPercent;

    @Column(name = "muscle_mass", precision = 10, scale = 2)
    private BigDecimal muscleMass;

    @Column(name = "waist_cm", precision = 10, scale = 2)
    private BigDecimal waistCm;

    @Column(name = "chest_cm", precision = 10, scale = 2)
    private BigDecimal chestCm;

    @Column(name = "hip_cm", precision = 10, scale = 2)
    private BigDecimal hipCm;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateMetrics(UpsertUserProgressEntryRequest request) {
        this.recordedAt = request.recordedAt();
        this.weight = request.weight();
        this.bodyFatPercent = request.bodyFatPercent();
        this.muscleMass = request.muscleMass();
        this.waistCm = request.waistCm();
        this.chestCm = request.chestCm();
        this.hipCm = request.hipCm();
        this.notes = request.notes();
    }

    public void markDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
}
