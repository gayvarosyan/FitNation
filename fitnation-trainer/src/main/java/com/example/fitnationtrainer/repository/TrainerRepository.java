package com.example.fitnationtrainer.repository;

import com.example.fitnationtrainer.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    List<Trainer> findAllByDeletedAtIsNull();

    Optional<Trainer> findByIdAndDeletedAtIsNull(Long id);
}
