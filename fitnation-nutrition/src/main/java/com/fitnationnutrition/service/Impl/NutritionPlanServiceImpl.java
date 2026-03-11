package com.fitnationnutrition.service.Impl;

import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.example.fitnationcommon.dto.response.NutritionStatsResponse;
import com.fitnationnutrition.model.NutritionPlan;
import com.fitnationnutrition.repository.NutritionPlanRepository;
import com.fitnationnutrition.repository.RatingRepository;
import com.fitnationnutrition.repository.UserNutritionPlanRepository;
import com.fitnationnutrition.service.NutritionPlanService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class NutritionPlanServiceImpl implements NutritionPlanService {

    private final NutritionPlanRepository planRepo;
    private final UserNutritionPlanRepository userPlanRepo;
    private final RatingRepository ratingRepo;

    @Override
    public NutritionStatsResponse getStats() {
        long totalPlans = planRepo.count();
        long activeUsers = userPlanRepo.countActiveUsers();
        return new NutritionStatsResponse(totalPlans, activeUsers);
    }

    @Override
    public List<NutritionPlanCatalogItemDto> getPlanCatalog() {
        return StreamSupport.stream(planRepo.findAll().spliterator(), false)
                .map(this::toCatalogItem)
                .toList();
    }

    @Override
    public NutritionPlanCatalogItemDto createPlan(CreateNutritionPlanRequest request) {
        NutritionPlan plan = new NutritionPlan();
        plan.setPlanName(request.getPlanName());
        plan.setCategory(request.getCategory());
        plan.setDescription(request.getDescription() != null ? request.getDescription() : "");
        plan.setPrice(request.getPrice() != null ? BigDecimal.valueOf(request.getPrice()) : null);
        plan.setStatus(request.getStatus() != null ? request.getStatus() : com.example.fitnationcommon.enums.PlanStatus.DRAFT);
        plan.setCreatedAt(LocalDateTime.now());
        NutritionPlan saved = planRepo.save(plan);
        return toCatalogItem(saved);
    }

    @Override
    public NutritionPlan createPlan(NutritionPlan plan) {
        return planRepo.save(plan);
    }

    private NutritionPlanCatalogItemDto toCatalogItem(NutritionPlan plan) {
        long activeClients = userPlanRepo.countActiveClients(plan.getId());
        Double avgRating = ratingRepo.getAverageRating(plan.getId());
        return new NutritionPlanCatalogItemDto(
                plan.getId(),
                plan.getPlanName(),
                plan.getCategory(),
                plan.getDescription(),
                plan.getPrice() != null ? plan.getPrice().doubleValue() : null,
                activeClients,
                avgRating != null ? avgRating : 0.0,
                plan.getStatus()
        );
    }
}