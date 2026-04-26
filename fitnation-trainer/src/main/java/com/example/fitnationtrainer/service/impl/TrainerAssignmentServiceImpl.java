package com.example.fitnationtrainer.service.impl;

import com.example.fitnationcommon.constants.ApplicationConstants;
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
import com.example.fitnationtrainer.service.TrainerAssignmentService;
import com.example.fitnationprogress.factory.NotificationCommandFactory;
import com.example.fitnationprogress.service.NotificationCommandPublisher;
import com.example.fitnationuser.repository.UserRepository;
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
public class TrainerAssignmentServiceImpl implements TrainerAssignmentService {

    private final TrainerAssignmentRequestRepository requestRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainerAssignmentRequestMapper mapper;
    private final TrainerMapper trainerMapper;
    private final NotificationCommandPublisher notificationCommandPublisher;

    @Override
    public List<TrainerDirectoryItem> getActiveTrainersForClients() {
        return trainerRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(trainerMapper::toDirectoryItem)
                .collect(Collectors.toList());
    }

    @Override
    public TrainerPublicProfileResponse getTrainerPublicProfile(Long trainerId, Long clientId) {
        var trainer = findActiveTrainerOrThrow(trainerId);
        var response = mapper.toPublicProfileResponse(trainer);

        var client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ApplicationConstants.MSG_USER_NOT_FOUND + clientId));

        var alreadyAssigned = trainerId.equals(client.getAssignedTrainerId());
        var hasPending = requestRepository.existsByClient_IdAndStatus(
                clientId, TrainerAssignmentRequestStatus.PENDING);
        var canRequest = !alreadyAssigned && !hasPending;

        var existingStatus = requestRepository
                .findByClient_IdAndStatus(clientId, TrainerAssignmentRequestStatus.PENDING)
                .filter(r -> r.getTrainer().getId().equals(trainerId))
                .map(TrainerAssignmentRequest::getStatus)
                .orElse(null);

        mapper.enrichWithClientContext(response, canRequest, existingStatus);
        return response;
    }

    @Override
    @Transactional
    public TrainerAssignmentRequestResponse createAssignmentRequest(
            Long clientId, CreateTrainerAssignmentRequest request) {

        var trainer = findActiveTrainerOrThrow(request.getTrainerId());

        var client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ApplicationConstants.MSG_USER_NOT_FOUND + clientId));

        if (requestRepository.existsByClient_IdAndStatus(clientId, TrainerAssignmentRequestStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ApplicationConstants.MSG_TRAINER_ASSIGNMENT_PENDING_EXISTS);
        }

        if (request.getTrainerId().equals(client.getAssignedTrainerId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ApplicationConstants.MSG_TRAINER_ALREADY_ASSIGNED);
        }

        var saved = requestRepository.save(
                TrainerAssignmentRequest.builder()
                        .client(client)
                        .trainer(trainer)
                        .status(TrainerAssignmentRequestStatus.PENDING)
                        .message(request.getMessage())
                        .build()
        );

        String clientDisplayName = formatPersonName(client.getFirstName(), client.getLastName());
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.trainerAssignmentRequested(
                        saved.getId(),
                        trainer.getId(),
                        clientDisplayName));

        return mapper.toResponse(saved);
    }

    @Override
    public List<TrainerAssignmentRequestResponse> getClientRequests(Long clientId) {
        return mapper.toResponseList(
                requestRepository.findByClient_IdOrderByCreatedAtDesc(clientId)
        );
    }

    @Override
    public List<TrainerAssignmentRequestResponse> getTrainerPendingRequests(Long trainerId) {
        return mapper.toResponseList(
                requestRepository.findByTrainer_IdAndStatusOrderByCreatedAtDesc(
                        trainerId, TrainerAssignmentRequestStatus.PENDING)
        );
    }

    @Override
    @Transactional
    public TrainerAssignmentRequestResponse approveRequest(
            Long trainerId, ApproveRejectTrainerRequestRequest request) {

        var tar = loadPendingRequestOwnedByTrainer(request.getRequestId(), trainerId);
        var client = tar.getClient();

        if (client.getAssignedTrainerId() != null && !client.getAssignedTrainerId().equals(trainerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ApplicationConstants.MSG_TRAINER_CLIENT_ALREADY_HAS_TRAINER);
        }

        tar.setStatus(TrainerAssignmentRequestStatus.APPROVED);
        tar.setResolvedAt(LocalDateTime.now());
        tar.setResolvedBy(trainerId);

        client.setAssignedTrainerId(trainerId);

        requestRepository.save(tar);
        userRepository.save(client);

        String trainerDisplayName = formatPersonName(tar.getTrainer().getFirstName(), tar.getTrainer().getLastName());
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.trainerAssignmentApproved(tar.getId(), client.getId(), trainerDisplayName));

        return mapper.toResponse(tar);
    }

    @Override
    @Transactional
    public TrainerAssignmentRequestResponse rejectRequest(
            Long trainerId, ApproveRejectTrainerRequestRequest request) {

        var tar = loadPendingRequestOwnedByTrainer(request.getRequestId(), trainerId);

        tar.setStatus(TrainerAssignmentRequestStatus.REJECTED);
        tar.setResolvedAt(LocalDateTime.now());
        tar.setResolvedBy(trainerId);

        requestRepository.save(tar);

        String trainerDisplayName = formatPersonName(tar.getTrainer().getFirstName(), tar.getTrainer().getLastName());
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.trainerAssignmentRejected(tar.getId(), tar.getClient().getId(), trainerDisplayName));

        return mapper.toResponse(tar);
    }

    private static String formatPersonName(String firstName, String lastName) {
        return firstName.trim() + " " + lastName.trim();
    }

    private Trainer findActiveTrainerOrThrow(Long trainerId) {
        return trainerRepository.findByIdAndDeletedAtIsNull(trainerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ApplicationConstants.MSG_TRAINER_NOT_FOUND + trainerId));
    }

    private TrainerAssignmentRequest loadPendingRequestOwnedByTrainer(Long requestId, Long trainerId) {
        var tar = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ApplicationConstants.MSG_TRAINER_ASSIGNMENT_REQUEST_NOT_FOUND + requestId));

        if (!tar.getTrainer().getId().equals(trainerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    ApplicationConstants.MSG_TRAINER_ASSIGNMENT_NOT_OWNER);
        }

        if (tar.getStatus() != TrainerAssignmentRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ApplicationConstants.MSG_TRAINER_ASSIGNMENT_NOT_PENDING + tar.getStatus().name().toLowerCase());
        }

        return tar;
    }
}