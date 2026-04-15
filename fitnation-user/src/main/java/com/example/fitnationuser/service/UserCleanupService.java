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

    @Scheduled(cron = "0 0 0 1/30 * ?")
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
        
        for (User user : usersToDelete) {
            log.debug("Permanently deleting user: {} (ID: {})", user.getEmail(), user.getId());
            userRepository.delete(user);
        }
        
        log.info("Completed cleanup of {} soft-deleted users", usersToDelete.size());
    }
}
