package com.example.fitnationtrainer.repository;

import com.example.fitnationtrainer.entity.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("""
    SELECT t FROM Trainer t
    WHERE t.deletedAt IS NULL
      AND (:status IS NULL OR t.status = :status)
      AND (:q IS NULL OR :q = '' OR
           LOWER(t.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR
           LOWER(t.lastName)  LIKE LOWER(CONCAT('%', :q, '%')) OR
           LOWER(t.email)     LIKE LOWER(CONCAT('%', :q, '%')))
    """)
    Page<Trainer> findPagedDirectory(
            @Param("q") String q,
            @Param("status") String status,
            Pageable pageable);

    List<Trainer> findAllByDeletedAtIsNull();

    Optional<Trainer> findByIdAndDeletedAtIsNull(Long id);
}
