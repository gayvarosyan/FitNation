package com.example.fitnationcommon.rbac;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RbacPolicyServiceTest {

    private RbacPolicyService rbacPolicyService;

    @BeforeEach
    void setUp() {
        rbacPolicyService = new RbacPolicyService();
    }
    @Test
    void requireAdmin_whenAdmin_doesNotThrow() {
        assertDoesNotThrow(() -> rbacPolicyService.requireAdmin(UserRole.ADMIN));
    }

    @Test
    void requireAdmin_whenClient_throws() {
        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> rbacPolicyService.requireAdmin(UserRole.CLIENT));
        assertTrue(ex.getMessage().startsWith("ROLE_NOT_ALLOWED"));
    }

    @Test
    void requireAdmin_whenTrainer_throws() {
        assertThrows(ForbiddenOperationException.class,
                () -> rbacPolicyService.requireAdmin(UserRole.TRAINER));
    }

    @Test
    void requireClientOrAdmin_whenClient_doesNotThrow() {
        assertDoesNotThrow(() -> rbacPolicyService.requireClientOrAdmin(UserRole.CLIENT));
    }

    @Test
    void requireClientOrAdmin_whenAdmin_doesNotThrow() {
        assertDoesNotThrow(() -> rbacPolicyService.requireClientOrAdmin(UserRole.ADMIN));
    }

    @Test
    void requireClientOrAdmin_whenTrainer_throws() {
        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> rbacPolicyService.requireClientOrAdmin(UserRole.TRAINER));
        assertTrue(ex.getMessage().startsWith("ROLE_NOT_ALLOWED"));
    }

    @Test
    void requireTrainerOrAdmin_whenTrainer_doesNotThrow() {
        assertDoesNotThrow(() -> rbacPolicyService.requireTrainerOrAdmin(UserRole.TRAINER));
    }

    @Test
    void requireTrainerOrAdmin_whenAdmin_doesNotThrow() {
        assertDoesNotThrow(() -> rbacPolicyService.requireTrainerOrAdmin(UserRole.ADMIN));
    }

    @Test
    void requireTrainerOrAdmin_whenClient_throws() {
        assertThrows(ForbiddenOperationException.class,
                () -> rbacPolicyService.requireTrainerOrAdmin(UserRole.CLIENT));
    }

    @Test
    void requireOwnershipOrAdmin_whenAdmin_doesNotThrow() {
        assertDoesNotThrow(() ->
                rbacPolicyService.requireOwnershipOrAdmin(1L, 2L, UserRole.ADMIN));
    }

    @Test
    void requireOwnershipOrAdmin_whenOwner_doesNotThrow() {
        assertDoesNotThrow(() ->
                rbacPolicyService.requireOwnershipOrAdmin(1L, 1L, UserRole.CLIENT));
    }

    @Test
    void requireOwnershipOrAdmin_whenNotOwner_throws() {
        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> rbacPolicyService.requireOwnershipOrAdmin(1L, 2L, UserRole.CLIENT));
        assertTrue(ex.getMessage().startsWith("OWNERSHIP_REQUIRED"));
    }
    @Test
    void requireAssignedTrainerOrAdmin_whenAdmin_doesNotThrow() {
        assertDoesNotThrow(() ->
                rbacPolicyService.requireAssignedTrainerOrAdmin(1L, 2L, UserRole.ADMIN));
    }

    @Test
    void requireAssignedTrainerOrAdmin_whenAssignedTrainer_doesNotThrow() {
        assertDoesNotThrow(() ->
                rbacPolicyService.requireAssignedTrainerOrAdmin(1L, 1L, UserRole.TRAINER));
    }

    @Test
    void requireAssignedTrainerOrAdmin_whenNotAssignedTrainer_throws() {
        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> rbacPolicyService.requireAssignedTrainerOrAdmin(1L, 2L, UserRole.TRAINER));
        assertTrue(ex.getMessage().startsWith("ACCESS_DENIED"));
    }

    @Test
    void requireAssignedTrainerOrAdmin_whenClient_throws() {
        assertThrows(ForbiddenOperationException.class,
                () -> rbacPolicyService.requireAssignedTrainerOrAdmin(1L, 1L, UserRole.CLIENT));
    }
}