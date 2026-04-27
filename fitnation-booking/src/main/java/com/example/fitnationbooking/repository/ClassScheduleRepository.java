package com.example.fitnationbooking.repository;

import com.example.fitnationbooking.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {

    @Query("""
            select s from ClassSchedule s
            join fetch s.groupClass gc
            join fetch gc.trainer t
            order by s.date asc, s.startTime asc
            """)
    List<ClassSchedule> findAllWithClassAndTrainer();

    @Query("""
            select s from ClassSchedule s
            join fetch s.groupClass gc
            join fetch gc.trainer t
            where (:dateFrom is null or s.date >= :dateFrom)
              and (:dateTo is null or s.date <= :dateTo)
              and (:trainerId is null or gc.trainer.id = :trainerId)
              and (:className is null or lower(gc.name) like lower(concat('%', :className, '%')))
            order by s.date asc, s.startTime asc
            """)
    List<ClassSchedule> findAllWithFilters(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("trainerId") Long trainerId,
            @Param("className") String className
    );

    @Query("""
            select s from ClassSchedule s
            join fetch s.groupClass gc
            join fetch gc.trainer t
            where s.id = :id
            """)
    Optional<ClassSchedule> findByIdWithClassAndTrainer(@Param("id") Long id);

    @Query("SELECT s FROM ClassSchedule s WHERE s.groupClass.trainer.id = :trainerId")
    List<ClassSchedule> findAllByGroupClassTrainerId(@Param("trainerId") Long trainerId);

    @Modifying
    @Query("""
            delete from ClassSchedule s
            where s.groupClass.trainer.id = :trainerId
            """)
    void deleteAllByTrainerId(@Param("trainerId") Long trainerId);
}