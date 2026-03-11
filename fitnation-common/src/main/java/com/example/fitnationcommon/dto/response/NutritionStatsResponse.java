package com.example.fitnationcommon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutritionStatsResponse {

    private long totalPlans;
    private long activeUsers;
}
