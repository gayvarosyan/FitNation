package com.example.fitnationuser.service;

import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCleanupService {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupSoftDeletedUsers() {
        log.info("Starting cleanup of soft-deleted users older than 30 days");

        var cutoffDate = LocalDateTime.now().minusDays(30);
        var usersToDelete = userRepository.findUsersDeletedBefore(cutoffDate);

        if (usersToDelete.isEmpty()) {
            log.info("No users found for permanent deletion");
            return;
        }

        log.info("Found {} users for permanent deletion", usersToDelete.size());

        int deletedCount = 0;

        try {
            userRepository.deleteAll(usersToDelete);
            deletedCount = usersToDelete.size();
        } catch (Exception e) {
            log.error("Batch delete failed, trying one by one", e);

            for (User user : usersToDelete) {
                try {
                    userRepository.delete(user);
                    deletedCount++;
                } catch (Exception ex) {
                    log.error("Failed to delete user {}", user.getId(), ex);
                }
            }
        }

        log.info("Completed cleanup of {} soft-deleted users", deletedCount);
    }
}