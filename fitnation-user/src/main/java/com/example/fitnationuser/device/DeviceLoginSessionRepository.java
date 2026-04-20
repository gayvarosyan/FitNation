package com.example.fitnationuser.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface DeviceLoginSessionRepository extends JpaRepository<DeviceLoginSession, String> {

    long countByInitiatorUserIdAndCreatedAtAfter(Long userId, LocalDateTime after);
}
