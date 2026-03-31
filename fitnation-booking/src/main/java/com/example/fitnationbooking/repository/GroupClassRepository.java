package com.example.fitnationbooking.repository;

import com.example.fitnationbooking.entity.GroupClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupClassRepository extends JpaRepository<GroupClass, Long> {

    @Query("SELECT g FROM GroupClass g JOIN FETCH g.trainer")
    List<GroupClass> findAllWithTrainer();

    List<GroupClass> findAllByTrainerId(Long trainerId);

    @Modifying
    @Query("""
            delete from GroupClass g
            where g.trainer.id = :trainerId
            """)
    void deleteAllByTrainerId(@Param("trainerId") Long trainerId);
}