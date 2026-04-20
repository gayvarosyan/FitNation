package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.ApproveRejectTrainerRequestRequest;
import com.example.fitnationcommon.dto.request.CreateTrainerAssignmentRequest;
import com.example.fitnationcommon.dto.response.TrainerAssignmentRequestResponse;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerPublicProfileResponse;
import com.example.fitnationrestapi.support.CurrentUserHelper;
import com.example.fitnationtrainer.service.impl.TrainerAssignmentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trainer-assignments")
@RequiredArgsConstructor
@Tag(name = "Trainer Assignments", description = "Trainer-client assignment management")
public class TrainerAssignmentController {

    private final TrainerAssignmentServiceImpl trainerAssignmentService;
    private final CurrentUserHelper currentUserHelper;

    @Operation(summary = "Get all active trainers for browsing")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Active trainers returned"))
    @GetMapping("/trainers")
    @PreAuthorize("hasRole('CLIENT')")
    public List<TrainerDirectoryItem> getActiveTrainers() {
        return trainerAssignmentService.getActiveTrainersForClients();
    }

    @Operation(summary = "Get trainer public profile")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Trainer profile returned"))
    @GetMapping("/trainers/{trainerId}")
    @PreAuthorize("hasRole('CLIENT')")
    public TrainerPublicProfileResponse getTrainerProfile(@PathVariable Long trainerId) {
        Long clientId = currentUserHelper.getId();
        return trainerAssignmentService.getTrainerPublicProfile(trainerId, clientId);
    }

    @Operation(summary = "Request trainer assignment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assignment request created"),
            @ApiResponse(responseCode = "400", description = "Validation error or business rule violation")
    })
    @PostMapping("/request")
    @PreAuthorize("hasRole('CLIENT')")
    public TrainerAssignmentRequestResponse requestTrainer(
            @Valid @RequestBody CreateTrainerAssignmentRequest request) {
        Long clientId = currentUserHelper.getId();
        return trainerAssignmentService.createAssignmentRequest(clientId, request);
    }

    @Operation(summary = "Get client's assignment requests")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Client requests returned"))
    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('CLIENT')")
    public List<TrainerAssignmentRequestResponse> getClientRequests() {
        Long clientId = currentUserHelper.getId();
        return trainerAssignmentService.getClientRequests(clientId);
    }

    @Operation(summary = "Get trainer's pending assignment requests")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Pending requests returned"))
    @GetMapping("/pending")
    @PreAuthorize("hasRole('TRAINER')")
    public List<TrainerAssignmentRequestResponse> getPendingRequests() {
        Long trainerId = currentUserHelper.getId();
        return trainerAssignmentService.getTrainerPendingRequests(trainerId);
    }

    @Operation(summary = "Approve trainer assignment request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request approved"),
            @ApiResponse(responseCode = "400", description = "Validation error or business rule violation")
    })
    @PostMapping("/approve")
    @PreAuthorize("hasRole('TRAINER')")
    public TrainerAssignmentRequestResponse approveRequest(
            @Valid @RequestBody ApproveRejectTrainerRequestRequest request) {
        Long trainerId = currentUserHelper.getId();
        return trainerAssignmentService.approveRequest(trainerId, request);
    }

    @Operation(summary = "Reject trainer assignment request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request rejected"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/reject")
    @PreAuthorize("hasRole('TRAINER')")
    public TrainerAssignmentRequestResponse rejectRequest(
            @Valid @RequestBody ApproveRejectTrainerRequestRequest request) {
        Long trainerId = currentUserHelper.getId();
        return trainerAssignmentService.rejectRequest(trainerId, request);
    }
}
