package com.example.fitnationtrainer.repository;

import com.example.fitnationcommon.enums.TrainerAssignmentRequestStatus;
import com.example.fitnationtrainer.entity.TrainerAssignmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerAssignmentRequestRepository extends JpaRepository<TrainerAssignmentRequest, Long> {

    boolean existsByClient_IdAndStatus(Long clientId, TrainerAssignmentRequestStatus status);

    Optional<TrainerAssignmentRequest> findByClient_IdAndStatus(Long clientId, TrainerAssignmentRequestStatus status);

    List<TrainerAssignmentRequest> findByClient_IdOrderByCreatedAtDesc(Long clientId);

    List<TrainerAssignmentRequest> findByTrainer_IdAndStatusOrderByCreatedAtDesc(Long trainerId, TrainerAssignmentRequestStatus status);
}