package com.fitnationnutrition.mapper;

import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.fitnationnutrition.model.NutritionPlan;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-28T01:59:08+0400",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class NutritionPlanMapperImpl implements NutritionPlanMapper {

    @Override
    public NutritionPlan toEntity(CreateNutritionPlanRequest request) {
        if ( request == null ) {
            return null;
        }

        NutritionPlan nutritionPlan = new NutritionPlan();

        nutritionPlan.setPlanName( request.getPlanName() );
        nutritionPlan.setCategory( request.getCategory() );
        nutritionPlan.setDescription( request.getDescription() );
        nutritionPlan.setStatus( request.getStatus() );

        nutritionPlan.setPrice( request.getPrice() != null ? java.math.BigDecimal.valueOf(request.getPrice()) : null );

        return nutritionPlan;
    }

    @Override
    public NutritionPlanCatalogItemDto toCatalogItem(NutritionPlan plan) {
        if ( plan == null ) {
            return null;
        }

        NutritionPlanCatalogItemDto nutritionPlanCatalogItemDto = new NutritionPlanCatalogItemDto();

        nutritionPlanCatalogItemDto.setId( plan.getId() );
        nutritionPlanCatalogItemDto.setPlanName( plan.getPlanName() );
        nutritionPlanCatalogItemDto.setCategory( plan.getCategory() );
        nutritionPlanCatalogItemDto.setDescription( plan.getDescription() );
        if ( plan.getPrice() != null ) {
            nutritionPlanCatalogItemDto.setPrice( plan.getPrice().doubleValue() );
        }
        nutritionPlanCatalogItemDto.setStatus( plan.getStatus() );

        return nutritionPlanCatalogItemDto;
    }
}
