package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.enums.PlanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNutritionPlanRequest {

    @NotBlank
    private String planName;

    private String category;

    private String description;

    @NotNull
    @PositiveOrZero
    private Double price;

    private PlanStatus status = PlanStatus.DRAFT;
}
