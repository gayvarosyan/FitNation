package com.example.fitnationbooking.repository;

import com.example.fitnationbooking.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {

    @Query("""
            select s from ClassSchedule s
            join fetch s.groupClass gc
            join fetch gc.trainer t
            """)
    List<ClassSchedule> findAllWithClassAndTrainer();
}