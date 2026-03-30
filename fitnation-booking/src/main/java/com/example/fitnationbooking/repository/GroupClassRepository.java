package com.example.fitnationbooking.repository;

import com.example.fitnationbooking.entity.GroupClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupClassRepository extends JpaRepository<GroupClass, Long> {

    @Query("SELECT g FROM GroupClass g JOIN FETCH g.trainer")
    List<GroupClass> findAllWithTrainer();
}