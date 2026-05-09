package com.example.fitnationweb.service;

import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.enums.PlanStatus;
import com.example.fitnationweb.support.MvcRedirect;
import com.fitnationnutrition.service.NutritionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class MvcNutritionAdminService {

    private static final String NUTRITION_PATH = "/admin/nutrition";

    private final NutritionPlanService nutritionPlanService;

    public void populatePageModel(Model model) {
        model.addAttribute("stats", nutritionPlanService.getStats());
        model.addAttribute("plans", nutritionPlanService.getPlanCatalog());
        model.addAttribute("navSection", "nutrition");
    }

    public MvcRedirect savePlan(
            Long planId,
            String planName,
            String category,
            double price,
            String description,
            PlanStatus status) {
        try {
            nutritionPlanService.savePlan(planId, toCreateRequest(planName, category, price, description, status));
            if (planId == null) {
                return MvcRedirect.to(NUTRITION_PATH, "Plan created successfully.");
            }
            return MvcRedirect.to(NUTRITION_PATH, "Plan updated successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(NUTRITION_PATH, e.getMessage());
        }
    }

    public MvcRedirect deletePlan(Long id) {
        try {
            nutritionPlanService.deletePlan(id);
            return MvcRedirect.to(NUTRITION_PATH, "Plan deleted successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(NUTRITION_PATH, e.getMessage());
        }
    }

    private static CreateNutritionPlanRequest toCreateRequest(
            String planName,
            String category,
            double price,
            String description,
            PlanStatus status) {
        CreateNutritionPlanRequest request = new CreateNutritionPlanRequest();
        request.setPlanName(trimOrNull(planName));
        request.setCategory(trimOrNull(category));
        request.setPrice(price);
        request.setDescription((description != null && !description.isBlank()) ? description.trim() : null);
        request.setStatus(status != null ? status : PlanStatus.DRAFT);
        return request;
    }

    private static String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}

