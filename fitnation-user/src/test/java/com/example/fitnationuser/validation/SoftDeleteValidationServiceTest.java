package com.example.fitnationuser.validation;

import com.example.fitnationcommon.exception.UserDeletedException;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SoftDeleteValidationServiceTest {

    private SoftDeleteValidationService softDeleteValidationService;

    @BeforeEach
    void setUp() {
        softDeleteValidationService = new SoftDeleteValidationService();
    }
    @Test

    void validateUserNotSoftDeleted_shouldAllowActiveUser() {
        User activeUser = createActiveUser(1L);

        assertDoesNotThrow(() -> softDeleteValidationService.validateUserNotSoftDeleted(activeUser));
    }

    @Test
    void validateUserForAuthentication_shouldAllowActiveUser() {
        User activeUser = createActiveUser(1L);

        assertDoesNotThrow(() -> softDeleteValidationService.validateUserForAuthentication(activeUser));
    }

    @Test
    void validateUserForBooking_shouldAllowActiveUser() {
        User activeUser = createActiveUser(1L);

        assertDoesNotThrow(() -> softDeleteValidationService.validateUserForBooking(activeUser));
    }

    @Test
    void validateUserForMembership_shouldAllowActiveUser() {
        User activeUser = createActiveUser(1L);

        assertDoesNotThrow(() -> softDeleteValidationService.validateUserForMembership(activeUser));
    }

    @Test
    void validateTrainerForOperations_shouldAllowActiveTrainer() {
        User activeTrainer = createActiveUser(1L);
        activeTrainer.setRole(UserRole.TRAINER);

        assertDoesNotThrow(() -> softDeleteValidationService.validateTrainerForOperations(activeTrainer));
    }

    @Test
    void validateClientForOperations_shouldAllowActiveClient() {
        User activeClient = createActiveUser(1L);
        activeClient.setRole(UserRole.CLIENT);

        assertDoesNotThrow(() -> softDeleteValidationService.validateClientForOperations(activeClient));
    }

    @Test
    void validateUserNotSoftDeleted_shouldThrowWhenUserDeletedByTimestamp() {
        User softDeletedUser = createSoftDeletedUserByTimestamp(1L);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateUserNotSoftDeleted(softDeletedUser));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateUserForAuthentication_shouldThrowWhenUserDeletedByTimestamp() {
        User softDeletedUser = createSoftDeletedUserByTimestamp(1L);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateUserForAuthentication(softDeletedUser));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateUserForBooking_shouldThrowWhenUserDeletedByTimestamp() {
        User softDeletedUser = createSoftDeletedUserByTimestamp(1L);


        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateUserForBooking(softDeletedUser));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateUserForMembership_shouldThrowWhenUserDeletedByTimestamp() {
        User softDeletedUser = createSoftDeletedUserByTimestamp(1L);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateUserForMembership(softDeletedUser));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateTrainerForOperations_shouldThrowWhenTrainerDeletedByTimestamp() {
        User softDeletedTrainer = createSoftDeletedUserByTimestamp(1L);
        softDeletedTrainer.setRole(UserRole.TRAINER);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateTrainerForOperations(softDeletedTrainer));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateClientForOperations_shouldThrowWhenClientDeletedByTimestamp() {
        User softDeletedClient = createSoftDeletedUserByTimestamp(1L);
        softDeletedClient.setRole(UserRole.CLIENT);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateClientForOperations(softDeletedClient));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateUserNotSoftDeleted_shouldThrowWhenUserDeletedByStatus() {
        User softDeletedUser = createSoftDeletedUserByStatus(1L);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateUserNotSoftDeleted(softDeletedUser));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateUserForAuthentication_shouldThrowWhenUserDeletedByStatus() {
        User softDeletedUser = createSoftDeletedUserByStatus(1L);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateUserForAuthentication(softDeletedUser));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateUserForBooking_shouldThrowWhenUserDeletedByStatus() {
        User softDeletedUser = createSoftDeletedUserByStatus(1L);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateUserForBooking(softDeletedUser));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateUserForMembership_shouldThrowWhenUserDeletedByStatus() {
        User softDeletedUser = createSoftDeletedUserByStatus(1L);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateUserForMembership(softDeletedUser));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateTrainerForOperations_shouldThrowWhenTrainerDeletedByStatus() {
        User softDeletedTrainer = createSoftDeletedUserByStatus(1L);
        softDeletedTrainer.setRole(UserRole.TRAINER);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateTrainerForOperations(softDeletedTrainer));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateClientForOperations_shouldThrowWhenClientDeletedByStatus() {
        User softDeletedClient = createSoftDeletedUserByStatus(1L);
        softDeletedClient.setRole(UserRole.CLIENT);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateClientForOperations(softDeletedClient));
        
        assertEquals(1L, exception.getUserId());
    }

    @Test
    void validateUserNotSoftDeleted_shouldThrowWhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> softDeleteValidationService.validateUserNotSoftDeleted(null));
        
        assertEquals("User cannot be null", exception.getMessage());
    }

    @Test
    void validateUserForAuthentication_shouldThrowWhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> softDeleteValidationService.validateUserForAuthentication(null));
        
        assertEquals("User cannot be null", exception.getMessage());
    }

    @Test
    void validateUsersNotSoftDeleted_shouldHandleEmptyList() {
        assertDoesNotThrow(() -> softDeleteValidationService.validateUsersNotSoftDeleted(List.of()));
    }

    @Test
    void validateUsersNotSoftDeleted_shouldHandleNullList() {
        assertDoesNotThrow(() -> softDeleteValidationService.validateUsersNotSoftDeleted(null));
    }

    @Test
    void validateUsersNotSoftDeleted_shouldThrowWhenAnyUserIsSoftDeleted() {
        User activeUser = createActiveUser(1L);
        User softDeletedUser = createSoftDeletedUserByTimestamp(2L);
        List<User> users = List.of(activeUser, softDeletedUser);

        UserDeletedException exception = assertThrows(UserDeletedException.class,
                () -> softDeleteValidationService.validateUsersNotSoftDeleted(users));
        
        assertEquals(2L, exception.getUserId());
    }

    @Test
    void isUserSoftDeleted_shouldReturnFalseForActiveUser() {
        User activeUser = createActiveUser(1L);

        boolean result = softDeleteValidationService.isUserSoftDeleted(activeUser);

        assertFalse(result);
    }

    @Test
    void isUserSoftDeleted_shouldReturnTrueForUserDeletedByTimestamp() {
        User softDeletedUser = createSoftDeletedUserByTimestamp(1L);

        boolean result = softDeleteValidationService.isUserSoftDeleted(softDeletedUser);

        assertTrue(result);
    }

    @Test
    void isUserSoftDeleted_shouldReturnTrueForUserDeletedByStatus() {
        User softDeletedUser = createSoftDeletedUserByStatus(1L);

        boolean result = softDeleteValidationService.isUserSoftDeleted(softDeletedUser);

        assertTrue(result);
    }

    @Test
    void isUserSoftDeleted_shouldReturnFalseForNullUser() {
        boolean result = softDeleteValidationService.isUserSoftDeleted(null);

        assertFalse(result);
    }

    @Test
    void filterSoftDeletedUsers_shouldReturnOnlyActiveUsers() {
        User activeUser1 = createActiveUser(1L);
        User activeUser2 = createActiveUser(2L);
        User softDeletedUser1 = createSoftDeletedUserByTimestamp(3L);
        User softDeletedUser2 = createSoftDeletedUserByStatus(4L);
        List<User> users = List.of(activeUser1, softDeletedUser1, activeUser2, softDeletedUser2);

        List<User> filteredUsers = softDeleteValidationService.filterSoftDeletedUsers(users);

        assertThat(filteredUsers).hasSize(2);
        assertThat(filteredUsers).containsExactly(activeUser1, activeUser2);
    }

    @Test
    void filterSoftDeletedUsers_shouldHandleEmptyList() {
        List<User> emptyList = List.of();

        List<User> filteredUsers = softDeleteValidationService.filterSoftDeletedUsers(emptyList);

        assertThat(filteredUsers).isEmpty();
    }

    @Test
    void filterSoftDeletedUsers_shouldHandleNullList() {
        List<User> filteredUsers = softDeleteValidationService.filterSoftDeletedUsers(null);

        assertThat(filteredUsers).isEmpty();
    }

    @Test
    void getSoftDeletionTimestamp_shouldReturnTimestampForSoftDeletedUser() {
        LocalDateTime deletionTime = LocalDateTime.now().minusDays(1);
        User softDeletedUser = createSoftDeletedUserByTimestamp(1L, deletionTime);

        LocalDateTime result = softDeleteValidationService.getSoftDeletionTimestamp(softDeletedUser);

        assertEquals(deletionTime, result);
    }

    @Test
    void getSoftDeletionTimestamp_shouldReturnCurrentTimeForUserDeletedByStatus() {
        User softDeletedUser = createSoftDeletedUserByStatus(1L);
        LocalDateTime before = LocalDateTime.now();

        LocalDateTime result = softDeleteValidationService.getSoftDeletionTimestamp(softDeletedUser);

        assertNotNull(result);
        assertTrue(result.isAfter(before.minusSeconds(1)));
        assertTrue(result.isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void getSoftDeletionTimestamp_shouldReturnNullForActiveUser() {
        User activeUser = createActiveUser(1L);

        LocalDateTime result = softDeleteValidationService.getSoftDeletionTimestamp(activeUser);

        assertNull(result);
    }

    @Test
    void validateUserForRestoration_shouldAllowSoftDeletedUser() {
        User softDeletedUser = createSoftDeletedUserByTimestamp(1L);

        assertDoesNotThrow(() -> softDeleteValidationService.validateUserForRestoration(softDeletedUser));
    }

    @Test
    void validateUserForRestoration_shouldThrowForActiveUser() {
        User activeUser = createActiveUser(1L);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> softDeleteValidationService.validateUserForRestoration(activeUser));
        
        assertEquals("User is not soft-deleted and cannot be restored: 1", exception.getMessage());
    }

    @Test
    void validateUserForRestoration_shouldThrowWhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> softDeleteValidationService.validateUserForRestoration(null));
        
        assertEquals("User cannot be null", exception.getMessage());
    }

    private User createActiveUser(Long id) {
        return User.builder()
                .id(id)
                .email("user" + id + "@test.com")
                .firstName("User")
                .lastName("Test")
                .password("password")
                .phone("123456789")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .deletedAt(null)
                .build();
    }

    private User createSoftDeletedUserByTimestamp(Long id) {
        return createSoftDeletedUserByTimestamp(id, LocalDateTime.now().minusDays(1));
    }

    private User createSoftDeletedUserByTimestamp(Long id, LocalDateTime deletedAt) {
        return User.builder()
                .id(id)
                .email("user" + id + "@test.com")
                .firstName("User")
                .lastName("Test")
                .password("password")
                .phone("123456789")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .deletedAt(deletedAt)
                .build();
    }

    private User createSoftDeletedUserByStatus(Long id) {
        return User.builder()
                .id(id)
                .email("user" + id + "@test.com")
                .firstName("User")
                .lastName("Test")
                .password("password")
                .phone("123456789")
                .role(UserRole.CLIENT)
                .status(UserStatus.DELETED)
                .deletedAt(null)
                .build();
    }
}
