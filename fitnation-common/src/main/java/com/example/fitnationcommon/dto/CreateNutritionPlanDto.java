package com.example.fitnationcommon.dto;

import com.example.fitnationcommon.enums.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNutritionPlanDto {

    private Long id;
    private String planName;
    private String category;
    private long activeClients;
    private Double avgRating;
    private PlanStatus status;
    private long totalPlans;
    private long activeUsers;

}
