package com.fitnationnutrition.service.Impl;

import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.example.fitnationcommon.dto.response.NutritionStatsResponse;
import com.fitnationnutrition.mapper.NutritionPlanMapper;
import com.fitnationnutrition.model.NutritionPlan;
import com.fitnationnutrition.repository.NutritionPlanRepository;
import com.fitnationnutrition.repository.RatingRepository;
import com.fitnationnutrition.repository.UserNutritionPlanRepository;
import com.fitnationnutrition.service.NutritionPlanService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class NutritionPlanServiceImpl implements NutritionPlanService {

    private final NutritionPlanRepository planRepo;
    private final UserNutritionPlanRepository userPlanRepo;
    private final RatingRepository ratingRepo;
    private final NutritionPlanMapper mapper;

    @Override
    public NutritionStatsResponse getStats() {
        long totalPlans = planRepo.count();
        long activeUsers = userPlanRepo.countActiveUsers();
        return new NutritionStatsResponse(totalPlans, activeUsers);
    }

    @Override
    public List<NutritionPlanCatalogItemDto> getPlanCatalog() {
        return StreamSupport.stream(planRepo.findAll().spliterator(), false)
                .map(mapper::toCatalogItem)
                .toList();
    }

    @Override
    public NutritionPlanCatalogItemDto createPlan(CreateNutritionPlanRequest request) {

        NutritionPlan plan = mapper.toEntity(request);

        NutritionPlan saved = planRepo.save(plan);

        return mapper.toCatalogItem(saved);
    }

    @Override
    public NutritionPlan createPlan(NutritionPlan plan) {
        return null;
    }
}