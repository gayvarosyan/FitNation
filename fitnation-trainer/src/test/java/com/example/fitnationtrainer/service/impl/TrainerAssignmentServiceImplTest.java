package com.example.fitnationtrainer.service.impl;

import com.example.fitnationcommon.dto.request.ApproveRejectTrainerRequestRequest;
import com.example.fitnationcommon.dto.request.CreateTrainerAssignmentRequest;
import com.example.fitnationcommon.dto.response.TrainerAssignmentRequestResponse;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerPublicProfileResponse;
import com.example.fitnationcommon.enums.TrainerAssignmentRequestStatus;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationtrainer.entity.Trainer;
import com.example.fitnationtrainer.entity.TrainerAssignmentRequest;
import com.example.fitnationtrainer.mapper.TrainerAssignmentRequestMapper;
import com.example.fitnationtrainer.mapper.TrainerMapper;
import com.example.fitnationtrainer.repository.TrainerAssignmentRequestRepository;
import com.example.fitnationtrainer.repository.TrainerRepository;
import com.example.fitnationuser.user.User;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.validation.SoftDeleteValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerAssignmentServiceImplTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private UserRepository userRepository;
    @Mock private TrainerAssignmentRequestRepository requestRepository;
    @Mock private TrainerAssignmentRequestMapper mapper;
    @Mock private TrainerMapper trainerMapper;
    @Mock private SoftDeleteValidationService softDeleteValidationService;

    @InjectMocks
    private TrainerAssignmentServiceImpl service;

    private User client;
    private Trainer trainer;
    private CreateTrainerAssignmentRequest request;

    @BeforeEach
    void setUp() {
        client = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .assignedTrainerId(null)
                .build();

        trainer = new Trainer();
        trainer.setId(2L);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setEmail("jane.smith@example.com");
        trainer.setRole(UserRole.TRAINER);
        trainer.setStatus(UserStatus.ACTIVE);
        trainer.setSpecialization("Fitness");
        trainer.setBio("Experienced trainer");

        request = new CreateTrainerAssignmentRequest();
        request.setTrainerId(2L);
        request.setMessage("I would like to train with you");
    }

    @Test
    void getActiveTrainersForClients_ReturnsMappedList() {
        var t1 = new Trainer();
        t1.setId(1L);
        var t2 = new Trainer();
        t2.setId(2L);

        var item1 = new TrainerDirectoryItem("1", "Jane", "Smith", "Yoga", "", "j@e.com", "000", UserStatus.ACTIVE, false);
        var item2 = new TrainerDirectoryItem("2", "Bob", "Lee", "HIIT", "", "b@e.com", "111", UserStatus.ACTIVE, false);

        when(trainerRepository.findAllByDeletedAtIsNull()).thenReturn(Arrays.asList(t1, t2));
        when(trainerMapper.toDirectoryItem(t1)).thenReturn(item1);
        when(trainerMapper.toDirectoryItem(t2)).thenReturn(item2);

        List<TrainerDirectoryItem> result = service.getActiveTrainersForClients();

        assertEquals(2, result.size());
        verify(trainerRepository).findAllByDeletedAtIsNull();
    }

    @Test
    void getActiveTrainersForClients_EmptyList_ReturnsEmpty() {
        when(trainerRepository.findAllByDeletedAtIsNull()).thenReturn(List.of());

        var result = service.getActiveTrainersForClients();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTrainerPublicProfile_CanRequest_WhenNoPendingAndNotAssigned() {
        var response = new TrainerPublicProfileResponse();
        when(trainerRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(trainer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mapper.toPublicProfileResponse(trainer)).thenReturn(response);
        when(requestRepository.existsByClient_IdAndStatus(1L, TrainerAssignmentRequestStatus.PENDING)).thenReturn(false);
        when(requestRepository.findByClient_IdAndStatus(1L, TrainerAssignmentRequestStatus.PENDING)).thenReturn(Optional.empty());
        
        doNothing().when(softDeleteValidationService).validateTrainerForOperations(trainer);
        doNothing().when(softDeleteValidationService).validateClientForOperations(client);

        var result = service.getTrainerPublicProfile(2L, 1L);

        assertNotNull(result);
        verify(mapper).enrichWithClientContext(response, true, null);
    }

    @Test
    void getTrainerPublicProfile_CanNotRequest_WhenAlreadyAssigned() {
        client.setAssignedTrainerId(2L);
        var response = new TrainerPublicProfileResponse();
        when(trainerRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(trainer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mapper.toPublicProfileResponse(trainer)).thenReturn(response);
        when(requestRepository.existsByClient_IdAndStatus(1L, TrainerAssignmentRequestStatus.PENDING)).thenReturn(false);
        when(requestRepository.findByClient_IdAndStatus(1L, TrainerAssignmentRequestStatus.PENDING)).thenReturn(Optional.empty());
        
        // Mock SoftDeleteValidationService to do nothing for active users
        doNothing().when(softDeleteValidationService).validateTrainerForOperations(trainer);
        doNothing().when(softDeleteValidationService).validateClientForOperations(client);

        service.getTrainerPublicProfile(2L, 1L);

        verify(mapper).enrichWithClientContext(response, false, null);
    }

    @Test
    void getTrainerPublicProfile_CanNotRequest_WhenHasPending() {
        var response = new TrainerPublicProfileResponse();
        when(trainerRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(trainer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mapper.toPublicProfileResponse(trainer)).thenReturn(response);
        when(requestRepository.existsByClient_IdAndStatus(1L, TrainerAssignmentRequestStatus.PENDING)).thenReturn(true);
        when(requestRepository.findByClient_IdAndStatus(1L, TrainerAssignmentRequestStatus.PENDING)).thenReturn(Optional.empty());
        
        doNothing().when(softDeleteValidationService).validateTrainerForOperations(trainer);
        doNothing().when(softDeleteValidationService).validateClientForOperations(client);

        service.getTrainerPublicProfile(2L, 1L);

        verify(mapper).enrichWithClientContext(response, false, null);
    }

    @Test
    void getTrainerPublicProfile_TrainerNotFound_ThrowsException() {
        when(trainerRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.empty());

        var ex = assertThrows(ResponseStatusException.class,
                () -> service.getTrainerPublicProfile(2L, 1L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void createAssignmentRequest_Success() {
        var saved = TrainerAssignmentRequest.builder()
                .id(100L)
                .client(client)
                .trainer(trainer)
                .status(TrainerAssignmentRequestStatus.PENDING)
                .message("I would like to train with you")
                .build();

        when(trainerRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(trainer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(requestRepository.existsByClient_IdAndStatus(1L, TrainerAssignmentRequestStatus.PENDING)).thenReturn(false);
        when(requestRepository.save(any(TrainerAssignmentRequest.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(new TrainerAssignmentRequestResponse());
        
        doNothing().when(softDeleteValidationService).validateTrainerForOperations(trainer);
        doNothing().when(softDeleteValidationService).validateClientForOperations(client);

        var result = service.createAssignmentRequest(1L, request);

        assertNotNull(result);
        verify(requestRepository).save(any(TrainerAssignmentRequest.class));
    }

    @Test
    void createAssignmentRequest_TrainerNotFound_ThrowsException() {
        when(trainerRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.empty());

        var ex = assertThrows(ResponseStatusException.class,
                () -> service.createAssignmentRequest(1L, request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void createAssignmentRequest_PendingRequestExists_ThrowsException() {
        when(trainerRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(trainer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(requestRepository.existsByClient_IdAndStatus(1L, TrainerAssignmentRequestStatus.PENDING)).thenReturn(true);

        var ex = assertThrows(ResponseStatusException.class,
                () -> service.createAssignmentRequest(1L, request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void createAssignmentRequest_TrainerAlreadyAssigned_ThrowsException() {
        client.setAssignedTrainerId(2L);

        when(trainerRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(trainer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(requestRepository.existsByClient_IdAndStatus(1L, TrainerAssignmentRequestStatus.PENDING)).thenReturn(false);

        var ex = assertThrows(ResponseStatusException.class,
                () -> service.createAssignmentRequest(1L, request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getClientRequests_ReturnsMappedList() {
        var tar = TrainerAssignmentRequest.builder()
                .id(1L).client(client).trainer(trainer)
                .status(TrainerAssignmentRequestStatus.PENDING).build();

        when(requestRepository.findByClient_IdOrderByCreatedAtDesc(1L)).thenReturn(List.of(tar));
        when(mapper.toResponseList(List.of(tar))).thenReturn(List.of(new TrainerAssignmentRequestResponse()));

        var result = service.getClientRequests(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getTrainerPendingRequests_ReturnsMappedList() {
        var tar = TrainerAssignmentRequest.builder()
                .id(1L).client(client).trainer(trainer)
                .status(TrainerAssignmentRequestStatus.PENDING).build();

        when(requestRepository.findByTrainer_IdAndStatusOrderByCreatedAtDesc(
                2L, TrainerAssignmentRequestStatus.PENDING)).thenReturn(List.of(tar));
        when(mapper.toResponseList(List.of(tar))).thenReturn(List.of(new TrainerAssignmentRequestResponse()));

        var result = service.getTrainerPendingRequests(2L);

        assertEquals(1, result.size());
    }

    @Test
    void approveRequest_Success() {
        var tar = TrainerAssignmentRequest.builder()
                .id(100L).client(client).trainer(trainer)
                .status(TrainerAssignmentRequestStatus.PENDING).build();

        var approveReq = new ApproveRejectTrainerRequestRequest();
        approveReq.setRequestId(100L);

        when(requestRepository.findById(100L)).thenReturn(Optional.of(tar));
        when(requestRepository.save(any())).thenReturn(tar);
        when(userRepository.save(any())).thenReturn(client);
        when(mapper.toResponse(any())).thenReturn(new TrainerAssignmentRequestResponse());

        var result = service.approveRequest(2L, approveReq);

        assertNotNull(result);
        assertEquals(TrainerAssignmentRequestStatus.APPROVED, tar.getStatus());
        assertEquals(2L, client.getAssignedTrainerId());
        assertNotNull(tar.getResolvedAt());
        assertEquals(2L, tar.getResolvedBy());
    }

    @Test
    void approveRequest_NotOwner_ThrowsException() {
        var tar = TrainerAssignmentRequest.builder()
                .id(100L).client(client).trainer(trainer)
                .status(TrainerAssignmentRequestStatus.PENDING).build();

        var approveReq = new ApproveRejectTrainerRequestRequest();
        approveReq.setRequestId(100L);

        when(requestRepository.findById(100L)).thenReturn(Optional.of(tar));

        var ex = assertThrows(ResponseStatusException.class,
                () -> service.approveRequest(99L, approveReq));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void approveRequest_NotPending_ThrowsException() {
        var tar = TrainerAssignmentRequest.builder()
                .id(100L).client(client).trainer(trainer)
                .status(TrainerAssignmentRequestStatus.REJECTED).build();

        var approveReq = new ApproveRejectTrainerRequestRequest();
        approveReq.setRequestId(100L);

        when(requestRepository.findById(100L)).thenReturn(Optional.of(tar));

        var ex = assertThrows(ResponseStatusException.class,
                () -> service.approveRequest(2L, approveReq));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void approveRequest_ClientHasDifferentTrainer_ThrowsException() {
        client.setAssignedTrainerId(99L);

        var tar = TrainerAssignmentRequest.builder()
                .id(100L).client(client).trainer(trainer)
                .status(TrainerAssignmentRequestStatus.PENDING).build();

        var approveReq = new ApproveRejectTrainerRequestRequest();
        approveReq.setRequestId(100L);

        when(requestRepository.findById(100L)).thenReturn(Optional.of(tar));

        var ex = assertThrows(ResponseStatusException.class,
                () -> service.approveRequest(2L, approveReq));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void rejectRequest_Success() {
        var tar = TrainerAssignmentRequest.builder()
                .id(100L).client(client).trainer(trainer)
                .status(TrainerAssignmentRequestStatus.PENDING).build();

        var rejectReq = new ApproveRejectTrainerRequestRequest();
        rejectReq.setRequestId(100L);

        when(requestRepository.findById(100L)).thenReturn(Optional.of(tar));
        when(requestRepository.save(any())).thenReturn(tar);
        when(mapper.toResponse(any())).thenReturn(new TrainerAssignmentRequestResponse());

        var result = service.rejectRequest(2L, rejectReq);

        assertNotNull(result);
        assertEquals(TrainerAssignmentRequestStatus.REJECTED, tar.getStatus());
        assertNull(client.getAssignedTrainerId());
        assertNotNull(tar.getResolvedAt());
        assertEquals(2L, tar.getResolvedBy());
    }

    @Test
    void rejectRequest_NotOwner_ThrowsException() {
        var tar = TrainerAssignmentRequest.builder()
                .id(100L).client(client).trainer(trainer)
                .status(TrainerAssignmentRequestStatus.PENDING).build();

        var rejectReq = new ApproveRejectTrainerRequestRequest();
        rejectReq.setRequestId(100L);

        when(requestRepository.findById(100L)).thenReturn(Optional.of(tar));

        var ex = assertThrows(ResponseStatusException.class,
                () -> service.rejectRequest(99L, rejectReq));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void rejectRequest_NotPending_ThrowsException() {
        var tar = TrainerAssignmentRequest.builder()
                .id(100L).client(client).trainer(trainer)
                .status(TrainerAssignmentRequestStatus.APPROVED).build();

        var rejectReq = new ApproveRejectTrainerRequestRequest();
        rejectReq.setRequestId(100L);

        when(requestRepository.findById(100L)).thenReturn(Optional.of(tar));

        var ex = assertThrows(ResponseStatusException.class,
                () -> service.rejectRequest(2L, rejectReq));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}