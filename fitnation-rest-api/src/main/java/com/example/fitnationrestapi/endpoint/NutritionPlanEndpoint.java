package com.example.fitnationrestapi.endpoint;

import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.example.fitnationcommon.dto.response.NutritionStatsResponse;
import com.fitnationnutrition.service.NutritionPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "Nutrition", description = "Nutrition plan catalog and admin maintenance")
public class NutritionPlanEndpoint {

    private final NutritionPlanService service;

    @Operation(summary = "Nutrition statistics")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Stats returned"))
    @GetMapping("/stats")
    public NutritionStatsResponse getStats() {
        return service.getStats();
    }

    @Operation(summary = "List nutrition plans")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Plans returned"))
    @GetMapping("/plans")
    public List<NutritionPlanCatalogItemDto> getPlans() {
        return service.getPlanCatalog();
    }

    @Operation(summary = "Create nutrition plan", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plan created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/plans")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public NutritionPlanCatalogItemDto createPlan(@Valid @RequestBody CreateNutritionPlanRequest request) {
        return service.createPlan(request);
    }

    @Operation(summary = "Update nutrition plan", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plan updated"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    @PutMapping("/plans/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NutritionPlanCatalogItemDto updatePlan(@PathVariable Long id,
                                                   @Valid @RequestBody CreateNutritionPlanRequest request) {
        return service.updatePlan(id, request);
    }

    @Operation(summary = "Delete nutrition plan", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Plan deleted"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    @DeleteMapping("/plans/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePlan(@PathVariable Long id) {
        service.deletePlan(id);
    }
}