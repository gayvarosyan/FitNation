package com.example.fitnationtrainer.repository;

import com.example.fitnationcommon.enums.TrainerAssignmentRequestStatus;
import com.example.fitnationtrainer.entity.TrainerAssignmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TrainerAssignmentRequestRepository extends JpaRepository<TrainerAssignmentRequest, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM TrainerAssignmentRequest r WHERE r.client.id = :clientId AND r.status = :status")
    boolean existsByClient_IdAndStatus(@Param("clientId") Long clientId, @Param("status") TrainerAssignmentRequestStatus status);

    @Query("SELECT r FROM TrainerAssignmentRequest r WHERE r.client.id = :clientId AND r.status = :status")
    Optional<TrainerAssignmentRequest> findByClient_IdAndStatus(@Param("clientId") Long clientId, @Param("status") TrainerAssignmentRequestStatus status);

    @Query("SELECT r FROM TrainerAssignmentRequest r WHERE r.client.id = :clientId ORDER BY r.createdAt DESC")
    List<TrainerAssignmentRequest> findByClient_IdOrderByCreatedAtDesc(@Param("clientId") Long clientId);

    @Query("SELECT r FROM TrainerAssignmentRequest r WHERE r.trainer.id = :trainerId AND r.status = :status ORDER BY r.createdAt DESC")
    List<TrainerAssignmentRequest> findByTrainer_IdAndStatusOrderByCreatedAtDesc(@Param("trainerId") Long trainerId, @Param("status") TrainerAssignmentRequestStatus status);

    @Query("""
        SELECT r FROM TrainerAssignmentRequest r
        WHERE r.client.id = :clientId
          AND r.status IN :statuses
    """)
    List<TrainerAssignmentRequest> findActiveByClientId(
            @Param("clientId") Long clientId,
            @Param("statuses") Set<TrainerAssignmentRequestStatus> statuses);

    @Query("""
        SELECT r FROM TrainerAssignmentRequest r
        WHERE r.trainer.id = :trainerId
          AND r.status IN :statuses
    """)
    List<TrainerAssignmentRequest> findActiveByTrainerId(
            @Param("trainerId") Long trainerId,
            @Param("statuses") Set<TrainerAssignmentRequestStatus> statuses);
}