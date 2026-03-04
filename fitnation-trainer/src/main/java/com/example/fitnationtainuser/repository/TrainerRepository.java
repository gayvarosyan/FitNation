package com.example.fitnationtainuser.repository;

import com.example.fitnationtainuser.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
}