package com.example.fitnationprogress.repository;

import com.example.fitnationprogress.model.UserProgressEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserProgressEntryRepository extends JpaRepository<UserProgressEntry, Long> {

    @Query("""
            SELECT e FROM UserProgressEntry e
            WHERE e.user.id = :userId AND e.deletedAt IS NULL
            """)
    Page<UserProgressEntry> findAllActiveByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT e FROM UserProgressEntry e
            WHERE e.id = :id AND e.deletedAt IS NULL
            """)
    Optional<UserProgressEntry> findActiveById(@Param("id") Long id);

    @Query("""
            SELECT e FROM UserProgressEntry e
            WHERE e.user.id = :userId AND e.deletedAt IS NULL
            ORDER BY e.recordedAt DESC, e.id DESC
            """)
    List<UserProgressEntry> findAllActiveByUserIdOrderByRecordedAtDesc(@Param("userId") Long userId);

    @Query("""
            SELECT COUNT(e) FROM UserProgressEntry e
            WHERE e.user.id = :userId AND e.deletedAt IS NULL
            """)
    long countActiveByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT e FROM UserProgressEntry e
            WHERE e.user.id = :userId
            AND e.deletedAt IS NULL
            AND e.recordedAt >= :fromInclusive
            ORDER BY e.recordedAt DESC, e.id DESC
            """)
    List<UserProgressEntry> findAllActiveByUserIdRecordedAfter(
            @Param("userId") Long userId,
            @Param("fromInclusive") LocalDateTime fromInclusive);
}
