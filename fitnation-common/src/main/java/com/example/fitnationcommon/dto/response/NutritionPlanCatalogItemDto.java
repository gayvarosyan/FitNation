package com.example.fitnationcommon.dto.response;

import com.example.fitnationcommon.enums.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutritionPlanCatalogItemDto {

    private Long id;
    private String planName;
    private String category;
    private String description;
    private Double price;
    private long activeClients;
    private Double avgRating;
    private PlanStatus status;
}
