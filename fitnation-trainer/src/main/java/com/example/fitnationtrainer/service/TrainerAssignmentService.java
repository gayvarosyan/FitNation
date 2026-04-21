package com.example.fitnationtrainer.service;

import com.example.fitnationcommon.dto.request.ApproveRejectTrainerRequestRequest;
import com.example.fitnationcommon.dto.request.CreateTrainerAssignmentRequest;
import com.example.fitnationcommon.dto.response.TrainerAssignmentRequestResponse;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerPublicProfileResponse;

import java.util.List;

public interface TrainerAssignmentService {

    List<TrainerDirectoryItem> getActiveTrainersForClients();

    TrainerPublicProfileResponse getTrainerPublicProfile(Long trainerId, Long clientId);

    TrainerAssignmentRequestResponse createAssignmentRequest(Long clientId, CreateTrainerAssignmentRequest request);

    List<TrainerAssignmentRequestResponse> getClientRequests(Long clientId);

    List<TrainerAssignmentRequestResponse> getTrainerPendingRequests(Long trainerId);

    TrainerAssignmentRequestResponse approveRequest(Long trainerId, ApproveRejectTrainerRequestRequest request);

    TrainerAssignmentRequestResponse rejectRequest(Long trainerId, ApproveRejectTrainerRequestRequest request);
}