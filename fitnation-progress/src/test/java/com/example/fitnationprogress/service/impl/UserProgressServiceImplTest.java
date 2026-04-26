package com.example.fitnationprogress.service.impl;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationprogress.dto.ProgressSummaryResponse;
import com.example.fitnationprogress.model.UserProgressEntry;
import com.example.fitnationprogress.repository.UserProgressEntryRepository;
import com.example.fitnationprogress.validation.UserProgressValidator;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import com.example.fitnationprogress.mapper.UserProgressMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProgressServiceImplTest {

    @Mock
    private UserProgressEntryRepository entryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserProgressMapper mapper;
    @Mock
    private UserProgressValidator validator;

    @InjectMocks
    private UserProgressServiceImpl service;

    @Test
    void getSummary_returnsTotalEntries() {
        User user = User.builder().id(10L).build();
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(entryRepository.findAllActiveByUserIdOrderByRecordedAtDesc(10L)).thenReturn(List.of(
                UserProgressEntry.builder()
                        .id(1L)
                        .user(user)
                        .recordedAt(LocalDateTime.now())
                        .weight(BigDecimal.valueOf(90))
                        .build(),
                UserProgressEntry.builder()
                        .id(2L)
                        .user(user)
                        .recordedAt(LocalDateTime.now().minusDays(2))
                        .weight(BigDecimal.valueOf(95))
                        .build()));
        when(entryRepository.countActiveByUserId(10L)).thenReturn(2L);
        when(entryRepository.findAllActiveByUserIdRecordedAfter(any(), any())).thenReturn(List.of());

        ProgressSummaryResponse response = service.getSummary(10L);

        assertEquals(2L, response.totalEntries());
    }

    @Test
    void getUserHistoryByActor_rejectsUnassignedTrainer() {
        User client = User.builder().id(77L).assignedTrainerId(11L).build();
        when(userRepository.findById(77L)).thenReturn(Optional.of(client));

        assertThrows(ForbiddenOperationException.class, () ->
                service.getUserHistoryByActor(22L, UserRole.TRAINER, 77L, null));
    }
}
