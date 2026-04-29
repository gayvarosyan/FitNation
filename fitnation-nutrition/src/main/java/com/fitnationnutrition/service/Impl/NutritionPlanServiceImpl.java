package com.fitnationnutrition.service.impl;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.example.fitnationcommon.dto.response.NutritionStatsResponse;
import com.example.fitnationcommon.exception.NutritionPlanNotFoundException;
import com.fitnationnutrition.mapper.NutritionPlanMapper;
import com.fitnationnutrition.model.NutritionPlan;
import com.fitnationnutrition.repository.NutritionPlanRepository;
import com.fitnationnutrition.repository.UserNutritionPlanRepository;
import com.fitnationnutrition.service.NutritionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class NutritionPlanServiceImpl implements NutritionPlanService {

    private final NutritionPlanRepository planRepo;
    private final UserNutritionPlanRepository userPlanRepo;
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
        return planRepo.save(plan);
    }

    @Override
    public NutritionPlanCatalogItemDto updatePlan(Long id, CreateNutritionPlanRequest request) {
        NutritionPlan existing = planRepo.findById(id)
                .orElseThrow(() -> new NutritionPlanNotFoundException(
                        ApplicationConstants.NUTRITION_PLAN_NOT_FOUND + id));

        existing.setPlanName(request.getPlanName());
        existing.setCategory(request.getCategory());
        existing.setDescription(request.getDescription());
        if (request.getPrice() != null) {
            existing.setPrice(BigDecimal.valueOf(request.getPrice()));
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        return mapper.toCatalogItem(planRepo.save(existing));
    }

    @Override
    public void deletePlan(Long id) {
        if (!planRepo.existsById(id)) {
            throw new NutritionPlanNotFoundException(
                    ApplicationConstants.NUTRITION_PLAN_NOT_FOUND + id);
        }
        planRepo.deleteById(id);
    }
}