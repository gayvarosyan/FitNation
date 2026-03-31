package com.example.fitnationweb.controller;

import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.enums.PlanStatus;
import com.example.fitnationweb.support.MvcRedirect;
import com.fitnationnutrition.service.NutritionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/nutrition")
@RequiredArgsConstructor
public class NutritionMvcController {

    private static final String NUTRITION_PATH = "/admin/nutrition";

    private final NutritionPlanService nutritionPlanService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public String page(Model model) {
        model.addAttribute("stats", nutritionPlanService.getStats());
        model.addAttribute("plans", nutritionPlanService.getPlanCatalog());
        model.addAttribute("navSection", "nutrition");
        return "admin/nutrition";
    }

    @PostMapping("/plans/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String savePlan(
            @RequestParam(required = false) Long planId,
            @RequestParam String planName,
            @RequestParam String category,
            @RequestParam double price,
            @RequestParam(required = false) String description,
            @RequestParam PlanStatus status,
            RedirectAttributes redirectAttributes) {
        var result = savePlanFromForm(planId, planName, category, price, description, status);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/plans/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = deletePlan(id);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    private MvcRedirect savePlanFromForm(
            Long planId,
            String planName,
            String category,
            double price,
            String description,
            PlanStatus status) {
        try {
            CreateNutritionPlanRequest request = new CreateNutritionPlanRequest();
            request.setPlanName(planName.trim());
            request.setCategory(category.trim());
            request.setPrice(price);
            request.setDescription(description != null && !description.isBlank() ? description.trim() : null);
            request.setStatus(status != null ? status : PlanStatus.DRAFT);
            nutritionPlanService.savePlan(planId, request);
            if (planId == null) {
                return MvcRedirect.to(NUTRITION_PATH, "Plan created successfully.");
            }
            return MvcRedirect.to(NUTRITION_PATH, "Plan updated successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(NUTRITION_PATH, e.getMessage());
        }
    }

    private MvcRedirect deletePlan(Long id) {
        try {
            nutritionPlanService.deletePlan(id);
            return MvcRedirect.to(NUTRITION_PATH, "Plan deleted successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(NUTRITION_PATH, e.getMessage());
        }
    }
}
