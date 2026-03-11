package com.fitnationnutrition.mapper;

import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.fitnationnutrition.model.NutritionPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public class NutritionPlanMapper {

    @Mapping(target = "description", expression = "java(request.getDescription() != null ? request.getDescription() : \"\")")
    @Mapping(target = "price", expression = "java(request.getPrice() != null ? java.math.BigDecimal.valueOf(request.getPrice()) : null)")
    @Mapping(target = "status", expression = "java(request.getStatus() != null ? request.getStatus() : PlanStatus.DRAFT)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    public NutritionPlan toEntity(CreateNutritionPlanRequest request) {
        return null;
    }

    public NutritionPlanCatalogItemDto toCatalogItem(NutritionPlan plan) {
        return null;
    }
}

