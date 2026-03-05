package com.example.fitnationtrainer.repository;

import com.example.fitnationtrainer.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
}
