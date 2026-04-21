package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.ApproveRejectTrainerRequestRequest;
import com.example.fitnationcommon.dto.request.CreateTrainerAssignmentRequest;
import com.example.fitnationcommon.dto.response.TrainerAssignmentRequestResponse;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerPublicProfileResponse;
import com.example.fitnationcommon.enums.TrainerAssignmentRequestStatus;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationrestapi.support.CurrentUserHelper;
import com.example.fitnationtrainer.service.impl.TrainerAssignmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerAssignmentEndpointTest {

    @Mock
    private TrainerAssignmentServiceImpl trainerAssignmentService;

    @Mock
    private CurrentUserHelper currentUserHelper;

    private TrainerAssignmentEndpoint controller;

    private TrainerAssignmentRequestResponse pendingResponse;
    private TrainerAssignmentRequestResponse approvedResponse;
    private TrainerAssignmentRequestResponse rejectedResponse;

    @BeforeEach
    void setUp() {
        controller = new TrainerAssignmentEndpoint(trainerAssignmentService, currentUserHelper);

        pendingResponse = TrainerAssignmentRequestResponse.builder()
                .id(1L).clientUserId(1L).clientFullName("John Doe")
                .trainerUserId(2L).trainerFullName("Jane Smith")
                .status(TrainerAssignmentRequestStatus.PENDING)
                .message("I would like to train with you")
                .createdAt(LocalDateTime.now()).build();

        approvedResponse = TrainerAssignmentRequestResponse.builder()
                .id(1L).clientUserId(1L).trainerUserId(2L)
                .status(TrainerAssignmentRequestStatus.APPROVED)
                .resolvedAt(LocalDateTime.now()).build();

        rejectedResponse = TrainerAssignmentRequestResponse.builder()
                .id(1L).clientUserId(1L).trainerUserId(2L)
                .status(TrainerAssignmentRequestStatus.REJECTED)
                .resolvedAt(LocalDateTime.now()).build();
    }

    @Test
    void getActiveTrainers_ReturnsListFromService() {
        var item = new TrainerDirectoryItem(
                "2", "Jane", "Smith", "Fitness", "Bio",
                "jane@test.com", "099000001", UserStatus.ACTIVE, false);

        when(trainerAssignmentService.getActiveTrainersForClients()).thenReturn(List.of(item));

        var result = controller.getActiveTrainers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Jane", result.getFirst().firstName());
        verify(trainerAssignmentService).getActiveTrainersForClients();
    }

    @Test
    void getActiveTrainers_EmptyList_ReturnsEmpty() {
        when(trainerAssignmentService.getActiveTrainersForClients()).thenReturn(List.of());

        var result = controller.getActiveTrainers();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTrainerProfile_ReturnsProfileFromService() {
        when(currentUserHelper.getId()).thenReturn(1L);

        var profile = TrainerPublicProfileResponse.builder()
                .userId(2L).firstName("Jane").lastName("Smith")
                .specialization("Fitness").canRequest(true).build();

        when(trainerAssignmentService.getTrainerPublicProfile(2L, 1L)).thenReturn(profile);

        var result = controller.getTrainerProfile(2L);

        assertNotNull(result);
        assertEquals(2L, result.getUserId());
        assertTrue(result.isCanRequest());
        verify(trainerAssignmentService).getTrainerPublicProfile(2L, 1L);
    }

    @Test
    void requestTrainer_ReturnsCreatedRequest() {
        when(currentUserHelper.getId()).thenReturn(1L);

        var request = new CreateTrainerAssignmentRequest();
        request.setTrainerId(2L);
        request.setMessage("I would like to train with you");

        when(trainerAssignmentService.createAssignmentRequest(eq(1L), any())).thenReturn(pendingResponse);

        var result = controller.requestTrainer(request);

        assertNotNull(result);
        assertEquals(TrainerAssignmentRequestStatus.PENDING, result.getStatus());
        assertEquals(2L, result.getTrainerUserId());
        verify(trainerAssignmentService).createAssignmentRequest(eq(1L), any());
    }

    @Test
    void getClientRequests_ReturnsListFromService() {
        when(currentUserHelper.getId()).thenReturn(1L);
        when(trainerAssignmentService.getClientRequests(1L)).thenReturn(List.of(pendingResponse));

        var result = controller.getClientRequests();

        assertFalse(result.isEmpty());
        assertEquals(TrainerAssignmentRequestStatus.PENDING, result.getFirst().getStatus());
        verify(trainerAssignmentService).getClientRequests(1L);
    }

    @Test
    void getPendingRequests_ReturnsListFromService() {
        when(currentUserHelper.getId()).thenReturn(2L);
        when(trainerAssignmentService.getTrainerPendingRequests(2L)).thenReturn(List.of(pendingResponse));

        var result = controller.getPendingRequests();

        assertFalse(result.isEmpty());
        assertEquals(TrainerAssignmentRequestStatus.PENDING, result.getFirst().getStatus());
        verify(trainerAssignmentService).getTrainerPendingRequests(2L);
    }

    @Test
    void approveRequest_ReturnsApprovedResponse() {
        when(currentUserHelper.getId()).thenReturn(2L);

        var request = new ApproveRejectTrainerRequestRequest();
        request.setRequestId(1L);

        when(trainerAssignmentService.approveRequest(eq(2L), any())).thenReturn(approvedResponse);

        var result = controller.approveRequest(request);

        assertNotNull(result);
        assertEquals(TrainerAssignmentRequestStatus.APPROVED, result.getStatus());
        assertNotNull(result.getResolvedAt());
        verify(trainerAssignmentService).approveRequest(eq(2L), any());
    }

    @Test
    void rejectRequest_ReturnsRejectedResponse() {
        when(currentUserHelper.getId()).thenReturn(2L);

        var request = new ApproveRejectTrainerRequestRequest();
        request.setRequestId(1L);

        when(trainerAssignmentService.rejectRequest(eq(2L), any())).thenReturn(rejectedResponse);

        var result = controller.rejectRequest(request);

        assertNotNull(result);
        assertEquals(TrainerAssignmentRequestStatus.REJECTED, result.getStatus());
        assertNotNull(result.getResolvedAt());
        verify(trainerAssignmentService).rejectRequest(eq(2L), any());
    }
}