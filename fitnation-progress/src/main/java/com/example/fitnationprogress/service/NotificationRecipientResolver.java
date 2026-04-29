package com.example.fitnationprogress.service;

import com.example.fitnationprogress.constants.NotificationContextKeys;
import com.example.fitnationprogress.dto.NotificationTriggerCommand;
import com.example.fitnationprogress.exception.InvalidNotificationContextException;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class NotificationRecipientResolver {

    private final UserRepository userRepository;

    public List<Long> resolve(RecipientAudience audience, NotificationTriggerCommand command) {
        return switch (audience) {
            case SUBJECT_USER -> singleId(parseRequiredLong(command, NotificationContextKeys.SUBJECT_USER_ID));
            case TRAINER_USER -> singleId(parseRequiredLong(command, NotificationContextKeys.TRAINER_USER_ID));
            case ALL_ACTIVE_ADMINS -> userRepository.findIdsByRoleAndStatus(UserRole.ADMIN, UserStatus.ACTIVE);
            case TRAINER_USER_AND_ADMINS -> mergeUnique(
                    optionalTrainer(command),
                    userRepository.findIdsByRoleAndStatus(UserRole.ADMIN, UserStatus.ACTIVE));
        };
    }

    private List<Long> singleId(Long id) {
        return id == null ? List.of() : List.of(id);
    }

    private List<Long> optionalTrainer(NotificationTriggerCommand command) {
        var raw = command.context().get(NotificationContextKeys.TRAINER_USER_ID);
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return List.of(Long.parseLong(raw.trim()));
    }

    private List<Long> mergeUnique(List<Long> trainerIds, List<Long> adminIds) {
        return Stream.concat(trainerIds.stream(), adminIds.stream())
                .distinct()
                .toList();
    }

    private Long parseRequiredLong(NotificationTriggerCommand command, String key) {
        var raw = command.context().get(key);
        if (raw == null || raw.isBlank()) {
            throw new InvalidNotificationContextException(key);
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException ex) {
            throw new InvalidNotificationContextException(key + "=" + raw);
        }
    }
}
