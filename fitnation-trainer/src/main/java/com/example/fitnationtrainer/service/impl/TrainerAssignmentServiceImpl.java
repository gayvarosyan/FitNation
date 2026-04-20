package com.example.fitnationtrainer.service.impl;

import com.example.fitnationcommon.dto.request.ApproveRejectTrainerRequestRequest;
import com.example.fitnationcommon.dto.request.CreateTrainerAssignmentRequest;
import com.example.fitnationcommon.dto.response.TrainerAssignmentRequestResponse;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerPublicProfileResponse;
import com.example.fitnationcommon.enums.TrainerAssignmentRequestStatus;
import com.example.fitnationtrainer.entity.Trainer;
import com.example.fitnationtrainer.entity.TrainerAssignmentRequest;
import com.example.fitnationtrainer.mapper.TrainerAssignmentRequestMapper;
import com.example.fitnationtrainer.mapper.TrainerMapper;
import com.example.fitnationtrainer.repository.TrainerAssignmentRequestRepository;
import com.example.fitnationtrainer.repository.TrainerRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerAssignmentServiceImpl {

    private final TrainerAssignmentRequestRepository requestRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainerAssignmentRequestMapper mapper;
    private final TrainerMapper trainerMapper;

    public List<TrainerDirectoryItem> getActiveTrainersForClients() {
        return trainerRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(trainerMapper::toDirectoryItem)
                .collect(Collectors.toList());
    }

    public TrainerPublicProfileResponse getTrainerPublicProfile(Long trainerId, Long clientId) {
        Trainer trainer = findActiveTrainerOrThrow(trainerId);
        TrainerPublicProfileResponse response = mapper.toPublicProfileResponse(trainer);

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        boolean alreadyAssigned = trainerId.equals(client.getAssignedTrainerId());
        boolean hasPending = requestRepository.existsByClient_IdAndStatus(clientId, TrainerAssignmentRequestStatus.PENDING);
        boolean canRequest = !alreadyAssigned && !hasPending;

        TrainerAssignmentRequestStatus existingStatus = requestRepository
                .findByClient_IdAndStatus(clientId, TrainerAssignmentRequestStatus.PENDING)
                .filter(r -> r.getTrainer().getId().equals(trainerId))
                .map(TrainerAssignmentRequest::getStatus)
                .orElse(null);

        mapper.enrichWithClientContext(response, canRequest, existingStatus);
        return response;
    }

    @Transactional
    public TrainerAssignmentRequestResponse createAssignmentRequest(
            Long clientId, CreateTrainerAssignmentRequest request) {

        Trainer trainer = findActiveTrainerOrThrow(request.getTrainerId());

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        if (requestRepository.existsByClient_IdAndStatus(clientId, TrainerAssignmentRequestStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You already have a pending trainer request.");
        }

        if (request.getTrainerId().equals(client.getAssignedTrainerId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This trainer is already your assigned trainer.");
        }

        TrainerAssignmentRequest saved = requestRepository.save(
                TrainerAssignmentRequest.builder()
                        .client(client)
                        .trainer(trainer)
                        .status(TrainerAssignmentRequestStatus.PENDING)
                        .message(request.getMessage())
                        .build()
        );

        return mapper.toResponse(saved);
    }

    public List<TrainerAssignmentRequestResponse> getClientRequests(Long clientId) {
        return mapper.toResponseList(
                requestRepository.findByClient_IdOrderByCreatedAtDesc(clientId)
        );
    }

    public List<TrainerAssignmentRequestResponse> getTrainerPendingRequests(Long trainerId) {
        return mapper.toResponseList(
                requestRepository.findByTrainer_IdAndStatusOrderByCreatedAtDesc(
                        trainerId, TrainerAssignmentRequestStatus.PENDING)
        );
    }

    @Transactional
    public TrainerAssignmentRequestResponse approveRequest(
            Long trainerId, ApproveRejectTrainerRequestRequest request) {

        TrainerAssignmentRequest tar = loadPendingRequestOwnedByTrainer(request.getRequestId(), trainerId);

        User client = tar.getClient();

        if (client.getAssignedTrainerId() != null && !client.getAssignedTrainerId().equals(trainerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Client already has an assigned trainer. They must unassign first.");
        }

        tar.setStatus(TrainerAssignmentRequestStatus.APPROVED);
        tar.setResolvedAt(LocalDateTime.now());
        tar.setResolvedBy(trainerId);

        client.setAssignedTrainerId(trainerId);

        requestRepository.save(tar);
        userRepository.save(client);

        return mapper.toResponse(tar);
    }

    @Transactional
    public TrainerAssignmentRequestResponse rejectRequest(
            Long trainerId, ApproveRejectTrainerRequestRequest request) {

        TrainerAssignmentRequest tar = loadPendingRequestOwnedByTrainer(request.getRequestId(), trainerId);

        tar.setStatus(TrainerAssignmentRequestStatus.REJECTED);
        tar.setResolvedAt(LocalDateTime.now());
        tar.setResolvedBy(trainerId);

        requestRepository.save(tar);

        return mapper.toResponse(tar);
    }

    private Trainer findActiveTrainerOrThrow(Long trainerId) {
        return trainerRepository.findByIdAndDeletedAtIsNull(trainerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found"));
    }

    private TrainerAssignmentRequest loadPendingRequestOwnedByTrainer(Long requestId, Long trainerId) {
        TrainerAssignmentRequest tar = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        if (!tar.getTrainer().getId().equals(trainerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This request does not belong to you");
        }

        if (tar.getStatus() != TrainerAssignmentRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Request is already " + tar.getStatus().name().toLowerCase());
        }

        return tar;
    }
}