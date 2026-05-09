package com.example.fitnationweb.controller;

import com.example.fitnationcommon.enums.PlanStatus;
import com.example.fitnationweb.service.MvcNutritionAdminService;
import com.example.fitnationweb.support.MvcRedirect;
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


    private final MvcNutritionAdminService mvcNutritionAdminService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String page(Model model) {
        mvcNutritionAdminService.populatePageModel(model);
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
        var result = mvcNutritionAdminService.savePlan(planId, planName, category, price, description, status);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/plans/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = mvcNutritionAdminService.deletePlan(id);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
