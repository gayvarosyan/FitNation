package com.example.fitnationbooking.repository;

import com.example.fitnationbooking.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {

    @Query("""
            select s from ClassSchedule s
            join fetch s.groupClass gc
            join fetch gc.trainer t
            """)
    List<ClassSchedule> findAllWithClassAndTrainer();

    @Query("""
            select s from ClassSchedule s
            join fetch s.groupClass gc
            join fetch gc.trainer t
            where s.id = :id
            """)
    Optional<ClassSchedule> findByIdWithClassAndTrainer(@Param("id") Long id);

    List<ClassSchedule> findAllByGroupClassTrainerId(Long trainerId);

    @Modifying
    @Query("""
            delete from ClassSchedule s
            where s.groupClass.trainer.id = :trainerId
            """)
    void deleteAllByTrainerId(@Param("trainerId") Long trainerId);
}