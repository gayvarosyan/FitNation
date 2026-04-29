package com.example.fitnationcommon.rbac;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import org.springframework.stereotype.Component;

@Component
public class RbacPolicyService {

    public void requireAdmin(UserRole role) {
        if (role != UserRole.ADMIN) {
            throw new ForbiddenOperationException(
                    "ROLE_NOT_ALLOWED: Admin access required");
        }
    }

    public void requireClientOrAdmin(UserRole role) {
        if (role != UserRole.CLIENT && role != UserRole.ADMIN) {
            throw new ForbiddenOperationException(
                    "ROLE_NOT_ALLOWED: Client or Admin access required");
        }
    }

    public void requireTrainerOrAdmin(UserRole role) {
        if (role != UserRole.TRAINER && role != UserRole.ADMIN) {
            throw new ForbiddenOperationException(
                    "ROLE_NOT_ALLOWED: Trainer or Admin access required");
        }
    }

    public void requireAnyRole(UserRole role) {
        if (role != UserRole.CLIENT && role != UserRole.TRAINER && role != UserRole.ADMIN) {
            throw new ForbiddenOperationException(
                    "ROLE_NOT_ALLOWED: Unknown role");
        }
    }

    public boolean isAdmin(UserRole role) {
        return role == UserRole.ADMIN;
    }

    public boolean isTrainer(UserRole role) {
        return role == UserRole.TRAINER;
    }

    public boolean isClient(UserRole role) {
        return role == UserRole.CLIENT;
    }


    public void requireOwnershipOrAdmin(Long requesterId, Long ownerId, UserRole role) {
        if (isAdmin(role)) return;
        if (!requesterId.equals(ownerId)) {
            throw new ForbiddenOperationException(
                    "OWNERSHIP_REQUIRED: You can only access your own resource");
        }
    }

    public void requireOwnership(Long requesterId, Long ownerId) {
        if (!requesterId.equals(ownerId)) {
            throw new ForbiddenOperationException(
                    "OWNERSHIP_REQUIRED: You can only access your own resource");
        }
    }

    public void requireAssignedTrainerOrAdmin(
            Long currentUserId, Long assignedTrainerId, UserRole role) {
        if (isAdmin(role)) return;
        if (isTrainer(role) && currentUserId.equals(assignedTrainerId)) return;
        throw new ForbiddenOperationException(
                "ACCESS_DENIED: Trainer can only access their assigned resources");
    }
}