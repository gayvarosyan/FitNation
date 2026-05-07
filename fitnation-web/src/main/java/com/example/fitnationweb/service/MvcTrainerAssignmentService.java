package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.request.ApproveRejectTrainerRequestRequest;
import com.example.fitnationcommon.dto.request.CreateTrainerAssignmentRequest;
import com.example.fitnationcommon.dto.response.TrainerAssignmentRequestResponse;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerPublicProfileResponse;
import com.example.fitnationtrainer.service.impl.TrainerAssignmentServiceImpl;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MvcTrainerAssignmentService {

    private static final String ASSIGNMENTS_PATH = "/trainer-assignments";

    private final TrainerAssignmentServiceImpl trainerAssignmentService;

    public List<TrainerDirectoryItem> getActiveTrainersForClients() {
        return trainerAssignmentService.getActiveTrainersForClients();
    }

    public TrainerPublicProfileResponse getTrainerPublicProfile(Long trainerId, Long clientId) {
        return trainerAssignmentService.getTrainerPublicProfile(trainerId, clientId);
    }

    public List<TrainerAssignmentRequestResponse> getClientRequests(Long clientId) {
        return trainerAssignmentService.getClientRequests(clientId);
    }

    public List<TrainerAssignmentRequestResponse> getTrainerPendingRequests(Long trainerId) {
        return trainerAssignmentService.getTrainerPendingRequests(trainerId);
    }

    public void populateActiveTrainersModel(Model model) {
        model.addAttribute("trainers", getActiveTrainersForClients());
        model.addAttribute("navSection", "trainer-assignments");
    }

    public void populateTrainerProfileModel(Long trainerId, Long clientId, Model model) {
        model.addAttribute("trainerProfile", getTrainerPublicProfile(trainerId, clientId));
        model.addAttribute("trainerId", trainerId);
        model.addAttribute("navSection", "trainer-assignments");
    }

    public void populateClientRequestsModel(Long clientId, Model model) {
        model.addAttribute("clientRequests", getClientRequests(clientId));
        model.addAttribute("navSection", "trainer-assignments");
    }

    public void populatePendingRequestsModel(Long trainerId, Model model) {
        model.addAttribute("trainerPendingRequests", getTrainerPendingRequests(trainerId));
        model.addAttribute("navSection", "trainer-assignments");
    }

    public MvcRedirect requestTrainer(Long clientId, CreateTrainerAssignmentRequest request) {
        try {
            trainerAssignmentService.createAssignmentRequest(clientId, request);
            return MvcRedirect.to(ASSIGNMENTS_PATH + "/my-requests", "Trainer assignment request sent successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(ASSIGNMENTS_PATH + "/my-requests", e.getMessage());
        }
    }

    public MvcRedirect approve(Long trainerId, ApproveRejectTrainerRequestRequest request) {
        try {
            trainerAssignmentService.approveRequest(trainerId, request);
            return MvcRedirect.to(ASSIGNMENTS_PATH + "/pending", "Assignment request approved.");
        } catch (Exception e) {
            return MvcRedirect.failure(ASSIGNMENTS_PATH + "/pending", e.getMessage());
        }
    }

    public MvcRedirect reject(Long trainerId, ApproveRejectTrainerRequestRequest request) {
        try {
            trainerAssignmentService.rejectRequest(trainerId, request);
            return MvcRedirect.to(ASSIGNMENTS_PATH + "/pending", "Assignment request rejected.");
        } catch (Exception e) {
            return MvcRedirect.failure(ASSIGNMENTS_PATH + "/pending", e.getMessage());
        }
    }
}

