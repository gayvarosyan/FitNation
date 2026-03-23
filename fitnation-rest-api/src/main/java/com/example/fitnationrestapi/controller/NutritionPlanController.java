package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.example.fitnationcommon.dto.response.NutritionStatsResponse;
import com.fitnationnutrition.service.NutritionPlanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
@AllArgsConstructor
public class NutritionPlanController {

    private final NutritionPlanService service;

    @GetMapping("/stats")
    public NutritionStatsResponse getStats() {
        return service.getStats();
    }

    @GetMapping("/plans")
    public List<NutritionPlanCatalogItemDto> getPlans() {
        return service.getPlanCatalog();
    }

    @PostMapping("/plans")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public NutritionPlanCatalogItemDto createPlan(@Valid @RequestBody CreateNutritionPlanRequest request) {
        return service.createPlan(request);
    }

    @PutMapping("/plans/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NutritionPlanCatalogItemDto updatePlan(@PathVariable Long id,
                                                   @Valid @RequestBody CreateNutritionPlanRequest request) {
        return service.updatePlan(id, request);
    }

    @DeleteMapping("/plans/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePlan(@PathVariable Long id) {
        service.deletePlan(id);
    }
}