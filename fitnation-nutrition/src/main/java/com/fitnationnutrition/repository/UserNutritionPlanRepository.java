package com.fitnationnutrition.repository;

import com.fitnationnutrition.model.UserNutritionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNutritionPlanRepository extends JpaRepository<UserNutritionPlan, Long> {

    @Query("SELECT COUNT(DISTINCT u.userId) FROM UserNutritionPlan u")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM UserNutritionPlan u WHERE u.plan.id = :planId")
    long countActiveClients(Long planId);
}