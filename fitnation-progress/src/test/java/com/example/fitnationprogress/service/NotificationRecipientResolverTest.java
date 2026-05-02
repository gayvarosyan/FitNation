package com.example.fitnationprogress.service;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationprogress.constants.NotificationContextKeys;
import com.example.fitnationprogress.dto.NotificationTriggerCommand;
import com.example.fitnationprogress.enums.NotificationEntityType;
import com.example.fitnationprogress.enums.NotificationEventType;
import com.example.fitnationprogress.exception.InvalidNotificationContextException;
import com.example.fitnationuser.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationRecipientResolverTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationRecipientResolver resolver;

    @Test
    void resolvesSubjectUser() {
        var cmd = NotificationTriggerCommand.of(
                NotificationEventType.CLASS_BOOKED,
                NotificationEntityType.CLASS_BOOKING,
                9L,
                Map.of(NotificationContextKeys.SUBJECT_USER_ID, "44"));
        assertEquals(List.of(44L), resolver.resolve(RecipientAudience.SUBJECT_USER, cmd));
    }

    @Test
    void resolvesAdminsFromRepository() {
        when(userRepository.findIdsByRoleAndStatus(UserRole.ADMIN, UserStatus.ACTIVE)).thenReturn(List.of(1L, 2L));
        var cmd = NotificationTriggerCommand.of(
                NotificationEventType.MEMBERSHIP_REQUEST_SUBMITTED,
                NotificationEntityType.MEMBERSHIP_REQUEST,
                3L,
                Map.of());
        assertEquals(List.of(1L, 2L), resolver.resolve(RecipientAudience.ALL_ACTIVE_ADMINS, cmd));
    }

    @Test
    void trainerAndAdminsMergesDistinctIds() {
        when(userRepository.findIdsByRoleAndStatus(UserRole.ADMIN, UserStatus.ACTIVE)).thenReturn(List.of(2L, 3L));
        var cmd = NotificationTriggerCommand.of(
                NotificationEventType.CLASS_FULL,
                NotificationEntityType.CLASS_SCHEDULE,
                5L,
                Map.of(NotificationContextKeys.TRAINER_USER_ID, "2"));
        assertEquals(List.of(2L, 3L), resolver.resolve(RecipientAudience.TRAINER_USER_AND_ADMINS, cmd));
    }

    @Test
    void subjectMissingThrows() {
        var cmd = NotificationTriggerCommand.of(
                NotificationEventType.CLASS_BOOKED,
                NotificationEntityType.CLASS_BOOKING,
                1L,
                Map.of());
        assertThrows(InvalidNotificationContextException.class,
                () -> resolver.resolve(RecipientAudience.SUBJECT_USER, cmd));
    }
}
