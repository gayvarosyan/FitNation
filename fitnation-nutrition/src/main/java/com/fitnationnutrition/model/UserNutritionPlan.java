package com.fitnationnutrition.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_nutrition_plans")
public class UserNutritionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "nutrition_plan_id")
    private NutritionPlan plan;

}
