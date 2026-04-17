package com.example.fitnationtrainer.service.impl;

import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.EmailAlreadyExistsException;
import com.example.fitnationcommon.exception.TrainerNotFoundException;
import com.example.fitnationcommon.exception.UserPendingException;
import com.example.fitnationcommon.service.EmailService;
import com.example.fitnationtrainer.entity.Trainer;
import com.example.fitnationtrainer.mapper.TrainerMapper;
import com.example.fitnationtrainer.repository.TrainerRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerManagementServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private TrainerManagementServiceImpl trainerManagementService;

    @Test
    void create_throwsWhenEmailAlreadyRegistered() {
        ReflectionTestUtils.setField(trainerManagementService, "loginUrl", "http://localhost/login");
        when(userRepository.findByEmail("dup@test.com")).thenReturn(Optional.of(new User()));

        CreateTrainerRequest req = new CreateTrainerRequest(
                "A", "B", "dup@test.com", "Secure1@x", "+1234567890", "Spec", "Bio");

        assertThrows(EmailAlreadyExistsException.class, () -> trainerManagementService.create(req));
        verifyNoInteractions(trainerRepository, trainerMapper, passwordEncoder, emailService);
    }

    @Test
    void edit_throwsWhenTrainerStillPending() {
        Trainer trainer = new Trainer();
        trainer.setId(3L);
        trainer.setStatus(UserStatus.PENDING);
        when(trainerRepository.findById(3L)).thenReturn(Optional.of(trainer));

        EditTrainerRequest req = new EditTrainerRequest(
                null, null, null, "+1999888777", "Spec", "Bio", null);

        assertThrows(UserPendingException.class, () ->
                trainerManagementService.edit(3L, req));
        verifyNoInteractions(trainerMapper);
    }

    @Test
    void edit_throwsWhenTrainerNotFound() {
        when(trainerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () ->
                trainerManagementService.edit(99L, new EditTrainerRequest(
                        null, null, null, "+1", "S", "B", null)));
    }
}
