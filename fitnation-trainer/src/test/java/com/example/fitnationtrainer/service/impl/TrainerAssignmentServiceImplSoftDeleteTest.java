package com.example.fitnationtrainer.service.impl;

import com.example.fitnationcommon.dto.request.CreateTrainerAssignmentRequest;
import com.example.fitnationcommon.dto.response.TrainerAssignmentRequestResponse;
import com.example.fitnationcommon.dto.response.TrainerPublicProfileResponse;
import com.example.fitnationcommon.enums.TrainerAssignmentRequestStatus;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.UserDeletedException;
import com.example.fitnationtrainer.entity.Trainer;
import com.example.fitnationuser.user.User;
import com.example.fitnationtrainer.entity.TrainerAssignmentRequest;
import com.example.fitnationtrainer.mapper.TrainerAssignmentRequestMapper;
import com.example.fitnationtrainer.mapper.TrainerMapper;
import com.example.fitnationtrainer.repository.TrainerAssignmentRequestRepository;
import com.example.fitnationtrainer.repository.TrainerRepository;
import com.example.fitnationprogress.service.NotificationCommandPublisher;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.validation.SoftDeleteValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerAssignmentServiceImplSoftDeleteTest {

    @Mock
    TrainerAssignmentRequestRepository requestRepository;
    @Mock
    TrainerRepository trainerRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    TrainerAssignmentRequestMapper mapper;
    @Mock
    TrainerMapper trainerMapper;
    @Mock
    NotificationCommandPublisher notificationCommandPublisher;
    @Mock
    SoftDeleteValidationService softDeleteValidationService;
    
    @InjectMocks
    TrainerAssignmentServiceImpl trainerAssignmentService;

    private User activeClient;
    private User    softDeletedClient;
    private Trainer activeTrainer;

    @BeforeEach
    void setUp() {
        LocalDateTime deletedAt = LocalDateTime.now().minusDays(1);

        activeClient = User.builder()
                .id(1L).firstName("John").lastName("Doe")
                .email("john@test.com").password("pass").phone("111")
                .role(UserRole.CLIENT).status(UserStatus.ACTIVE)
                .deletedAt(null).build();

        softDeletedClient = User.builder()
                .id(2L).firstName("Jane").lastName("Smith")
                .email("jane@test.com").password("pass").phone("222")
                .role(UserRole.CLIENT).status(UserStatus.DELETED)
                .deletedAt(deletedAt).build();

        activeTrainer = new Trainer();
        activeTrainer.setId(3L);
        activeTrainer.setFirstName("Mike");
        activeTrainer.setLastName("Johnson");
        activeTrainer.setEmail("mike@test.com");
        activeTrainer.setPassword("pass");
        activeTrainer.setPhone("333");
        activeTrainer.setRole(UserRole.TRAINER);
        activeTrainer.setStatus(UserStatus.ACTIVE);
        activeTrainer.setDeletedAt(null);
        activeTrainer.setSpecialization("Fitness");
    }

    @Test
    @DisplayName("Success — active client + active trainer")
    void createAssignmentRequest_success_whenBothActive() {
        when(trainerRepository.findByIdAndDeletedAtIsNull(3L))
                .thenReturn(Optional.of(activeTrainer));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(activeClient));
        when(requestRepository.existsByClient_IdAndStatus(1L, TrainerAssignmentRequestStatus.PENDING))
                .thenReturn(false);
        when(requestRepository.save(any()))
                .thenReturn(TrainerAssignmentRequest.builder().id(10L).build());
        when(mapper.toResponse(any()))
                .thenReturn(TrainerAssignmentRequestResponse.builder().build());
        
        org.mockito.Mockito.doNothing().when(softDeleteValidationService).validateTrainerForOperations(activeTrainer);
        org.mockito.Mockito.doNothing().when(softDeleteValidationService).validateClientForOperations(activeClient);

        assertThatNoException().isThrownBy(() ->
                trainerAssignmentService.createAssignmentRequest(1L,
                        new CreateTrainerAssignmentRequest(3L, "I need training")));

        verify(requestRepository).save(any(TrainerAssignmentRequest.class));
    }

    @Test
    @DisplayName("Fail — soft-deleted client cannot request assignment → UserDeletedException")
    void createAssignmentRequest_fails_whenClientSoftDeleted() {
        when(trainerRepository.findByIdAndDeletedAtIsNull(3L))
                .thenReturn(Optional.of(activeTrainer));
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(softDeletedClient));
        
        org.mockito.Mockito.doThrow(new UserDeletedException(2L))
                .when(softDeleteValidationService).validateClientForOperations(softDeletedClient);

        assertThatThrownBy(() ->
                trainerAssignmentService.createAssignmentRequest(2L,
                        new CreateTrainerAssignmentRequest(3L, "I need training")))
                .isInstanceOf(UserDeletedException.class);

        verify(requestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fail — soft-deleted trainer → ResponseStatusException 404")
    void createAssignmentRequest_fails_whenTrainerSoftDeleted() {
        when(trainerRepository.findByIdAndDeletedAtIsNull(4L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                trainerAssignmentService.createAssignmentRequest(1L,
                        new CreateTrainerAssignmentRequest(4L, "I need training")))
                .isInstanceOf(ResponseStatusException.class);

        verify(requestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Invariant A — only active trainers returned")
    void getActiveTrainersForClients_excludesSoftDeletedTrainers() {
        when(trainerRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of(activeTrainer));
        when(trainerMapper.toDirectoryItem(activeTrainer))
                .thenReturn(new com.example.fitnationcommon.dto.response.TrainerDirectoryItem(
                        "3", "Mike", "Johnson", "Fitness", null, "mike@test.com", "333", UserStatus.ACTIVE, false));

        var result = trainerAssignmentService.getActiveTrainersForClients();

        assertThat(result).hasSize(1);
        verify(trainerRepository).findAllByDeletedAtIsNull();
    }

    @Test
    @DisplayName("Invariant A — empty list when all trainers deleted")
    void getActiveTrainersForClients_returnsEmpty_whenAllDeleted() {
        when(trainerRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of());

        var result = trainerAssignmentService.getActiveTrainersForClients();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Success — active client views active trainer profile")
    void getTrainerPublicProfile_success_whenBothActive() {
        when(trainerRepository.findByIdAndDeletedAtIsNull(3L))
                .thenReturn(Optional.of(activeTrainer));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(activeClient));
        when(mapper.toPublicProfileResponse(activeTrainer))
                .thenReturn(TrainerPublicProfileResponse.builder().build());
        when(requestRepository.existsByClient_IdAndStatus(any(), any()))
                .thenReturn(false);
        when(requestRepository.findByClient_IdAndStatus(any(), any()))
                .thenReturn(Optional.empty());
        
        org.mockito.Mockito.doNothing().when(softDeleteValidationService).validateTrainerForOperations(activeTrainer);
        org.mockito.Mockito.doNothing().when(softDeleteValidationService).validateClientForOperations(activeClient);

        assertThatNoException().isThrownBy(() ->
                trainerAssignmentService.getTrainerPublicProfile(3L, 1L));
    }

    @Test
    @DisplayName("Fail — soft-deleted trainer → ResponseStatusException 404")
    void getTrainerPublicProfile_fails_whenTrainerSoftDeleted() {
        when(trainerRepository.findByIdAndDeletedAtIsNull(4L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                trainerAssignmentService.getTrainerPublicProfile(4L, 1L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("Fail — soft-deleted client views profile → UserDeletedException")
    void getTrainerPublicProfile_fails_whenClientSoftDeleted() {
        when(trainerRepository.findByIdAndDeletedAtIsNull(3L))
                .thenReturn(Optional.of(activeTrainer));
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(softDeletedClient));
        
        org.mockito.Mockito.doThrow(new UserDeletedException(2L))
                .when(softDeleteValidationService).validateClientForOperations(softDeletedClient);

        assertThatThrownBy(() ->
                trainerAssignmentService.getTrainerPublicProfile(3L, 2L))
                .isInstanceOf(UserDeletedException.class);

        verify(requestRepository, never()).existsByClient_IdAndStatus(any(), any());
    }
}