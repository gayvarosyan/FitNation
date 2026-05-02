package com.example.fitnationuser.validation;

import com.example.fitnationcommon.exception.UserDeletedException;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SoftDeleteValidationService {

    public void validateUserNotSoftDeleted(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (isUserSoftDeleted(user)) {
            log.warn("Soft-deleted user attempted access: userId={}, email={}", 
                    user.getId(), user.getEmail());
            throw new UserDeletedException(user.getId());
        }
    }

    public void validateUsersNotSoftDeleted(List<User> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        
        for (User user : users) {
            validateUserNotSoftDeleted(user);
        }
    }

    public void validateUserForAuthentication(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (isUserSoftDeleted(user)) {
            log.warn("Soft-deleted user attempted authentication: userId={}, email={}, deletedAt={}", 
                    user.getId(), user.getEmail(), user.getDeletedAt());
            throw new UserDeletedException(user.getId());
        }
    }

    public void validateUserForBooking(User user) {
        validateUserNotSoftDeleted(user);
        log.debug("User validated for booking operations: userId={}", user.getId());
    }

    public void validateUserForMembership(User user) {
        validateUserNotSoftDeleted(user);
        log.debug("User validated for membership operations: userId={}", user.getId());
    }

    public void validateTrainerForOperations(User trainer) {
        validateUserNotSoftDeleted(trainer);
        log.debug("Trainer validated for operations: trainerId={}", trainer.getId());
    }

    public void validateClientForOperations(User client) {
        validateUserNotSoftDeleted(client);
        log.debug("Client validated for operations: clientId={}", client.getId());
    }

    public boolean isUserSoftDeleted(User user) {
        if (user == null) {
            return false;
        }
        
        boolean deletedByTimestamp = user.getDeletedAt() != null;
        boolean deletedByStatus = com.example.fitnationcommon.enums.UserStatus.DELETED.equals(user.getStatus());
        
        return deletedByTimestamp || deletedByStatus;
    }

    public List<User> filterSoftDeletedUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        
        return users.stream()
                .filter(user -> !isUserSoftDeleted(user))
                .toList();
    }

    public LocalDateTime getSoftDeletionTimestamp(User user) {
        if (isUserSoftDeleted(user)) {
            return user.getDeletedAt() != null ? user.getDeletedAt() : LocalDateTime.now();
        }
        return null;
    }

    public void validateUserForRestoration(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (!isUserSoftDeleted(user)) {
            throw new IllegalStateException("User is not soft-deleted and cannot be restored: " + user.getId());
        }
        
        log.debug("User validated for restoration: userId={}", user.getId());
    }
}
