package com.fitnationnutrition.service;

import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.example.fitnationcommon.dto.response.NutritionStatsResponse;
import com.fitnationnutrition.model.NutritionPlan;

import java.util.List;

public interface NutritionPlanService {

    NutritionStatsResponse getStats();

    List<NutritionPlanCatalogItemDto> getPlanCatalog();

    NutritionPlanCatalogItemDto createPlan(CreateNutritionPlanRequest request);

    NutritionPlan createPlan(NutritionPlan plan);
}