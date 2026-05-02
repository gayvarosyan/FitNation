package com.example.fitnationuser.service;

import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.event.UserSoftDeletedEvent;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository       userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void softDeleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getDeletedAt() != null) {
            log.info("[SoftDelete] User {} already deleted — no-op", id);
            return;
        }

        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserSoftDeletedEvent(
                this,
                user.getId(),
                user.getRole(),
                user.getAssignedTrainerId(),
                user.getAssignedNutritionPlanId()
        ));

        log.info("[AUDIT] User {} soft-deleted at {}", id, LocalDateTime.now());
    }

    @Transactional
    public void restoreUser(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new UserNotFoundException("Deleted user not found"));

        user.setDeletedAt(null);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        log.info("[AUDIT] User {} restored at {}", id, LocalDateTime.now());
    }
}