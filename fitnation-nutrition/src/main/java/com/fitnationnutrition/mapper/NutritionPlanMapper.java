package com.fitnationnutrition.mapper;

import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.fitnationnutrition.model.NutritionPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NutritionPlanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "price", expression = "java(request.getPrice() != null ? java.math.BigDecimal.valueOf(request.getPrice()) : null)")
    NutritionPlan toEntity(CreateNutritionPlanRequest request);

    NutritionPlanCatalogItemDto toCatalogItem(NutritionPlan plan);
}