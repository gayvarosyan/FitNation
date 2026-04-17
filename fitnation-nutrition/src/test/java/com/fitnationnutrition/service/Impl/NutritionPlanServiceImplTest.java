package com.fitnationnutrition.service.impl;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.CreateNutritionPlanRequest;
import com.example.fitnationcommon.dto.response.NutritionPlanCatalogItemDto;
import com.example.fitnationcommon.dto.response.NutritionStatsResponse;
import com.example.fitnationcommon.enums.PlanStatus;
import com.example.fitnationcommon.exception.NutritionPlanNotFoundException;
import com.fitnationnutrition.mapper.NutritionPlanMapper;
import com.fitnationnutrition.model.NutritionPlan;
import com.fitnationnutrition.repository.NutritionPlanRepository;
import com.fitnationnutrition.repository.UserNutritionPlanRepository;
import com.fitnationnutrition.service.NutritionPlanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NutritionPlanServiceImplTest {

    @Mock
    private NutritionPlanRepository planRepo;
    @Mock
    private UserNutritionPlanRepository userPlanRepo;
    @Mock
    private NutritionPlanMapper mapper;

    @InjectMocks
    private NutritionPlanServiceImpl nutritionPlanService;

    @Test
    void getStats_returnsTotalPlansAndActiveUsers() {
        when(planRepo.count()).thenReturn(5L);
        when(userPlanRepo.countActiveUsers()).thenReturn(12L);

        NutritionStatsResponse stats = nutritionPlanService.getStats();

        assertEquals(5L, stats.getTotalPlans());
        assertEquals(12L, stats.getActiveUsers());
    }

    @Test
    void getPlanCatalog_mapsEachPlan() {
        NutritionPlan p1 = new NutritionPlan();
        p1.setId(1L);
        NutritionPlan p2 = new NutritionPlan();
        p2.setId(2L);
        when(planRepo.findAll()).thenReturn(List.of(p1, p2));

        NutritionPlanCatalogItemDto dto1 = new NutritionPlanCatalogItemDto();
        dto1.setId(1L);
        NutritionPlanCatalogItemDto dto2 = new NutritionPlanCatalogItemDto();
        dto2.setId(2L);
        when(mapper.toCatalogItem(p1)).thenReturn(dto1);
        when(mapper.toCatalogItem(p2)).thenReturn(dto2);

        List<NutritionPlanCatalogItemDto> catalog = nutritionPlanService.getPlanCatalog();

        assertEquals(2, catalog.size());
        assertEquals(1L, catalog.get(0).getId());
        assertEquals(2L, catalog.get(1).getId());
    }

    @Test
    void createPlan_persistsAndReturnsCatalogItem() {
        CreateNutritionPlanRequest request = new CreateNutritionPlanRequest(
                "Lean bulk", "Muscle", "High protein", 49.99, PlanStatus.DRAFT);
        NutritionPlan entity = new NutritionPlan();
        NutritionPlan saved = new NutritionPlan();
        saved.setId(10L);
        saved.setPlanName("Lean bulk");
        NutritionPlanCatalogItemDto dto = new NutritionPlanCatalogItemDto();
        dto.setId(10L);
        dto.setPlanName("Lean bulk");

        when(mapper.toEntity(request)).thenReturn(entity);
        when(planRepo.save(entity)).thenReturn(saved);
        when(mapper.toCatalogItem(saved)).thenReturn(dto);

        NutritionPlanCatalogItemDto result = nutritionPlanService.createPlan(request);

        assertEquals(10L, result.getId());
        assertEquals("Lean bulk", result.getPlanName());
        verify(planRepo).save(entity);
    }

    @Test
    void updatePlan_updatesFieldsAndReturnsCatalogItem() {
        Long id = 7L;
        CreateNutritionPlanRequest request = new CreateNutritionPlanRequest(
                "Cut", "Fat loss", "Calorie deficit", 39.0, PlanStatus.ACTIVE);
        NutritionPlan existing = new NutritionPlan();
        existing.setId(id);
        existing.setPlanName("Old");
        existing.setCategory("Old cat");
        existing.setDescription("Old desc");
        existing.setPrice(BigDecimal.TEN);
        existing.setStatus(PlanStatus.DRAFT);

        NutritionPlan saved = new NutritionPlan();
        saved.setId(id);
        NutritionPlanCatalogItemDto dto = new NutritionPlanCatalogItemDto();
        dto.setId(id);

        when(planRepo.findById(id)).thenReturn(Optional.of(existing));
        when(planRepo.save(existing)).thenReturn(saved);
        when(mapper.toCatalogItem(saved)).thenReturn(dto);

        NutritionPlanCatalogItemDto result = nutritionPlanService.updatePlan(id, request);

        assertEquals(id, result.getId());
        assertEquals("Cut", existing.getPlanName());
        assertEquals("Fat loss", existing.getCategory());
        assertEquals("Calorie deficit", existing.getDescription());
        assertEquals(0, BigDecimal.valueOf(39.0).compareTo(existing.getPrice()));
        assertEquals(PlanStatus.ACTIVE, existing.getStatus());
    }

    @Test
    void updatePlan_throwsWhenPlanMissing() {
        when(planRepo.findById(99L)).thenReturn(Optional.empty());

        NutritionPlanNotFoundException ex = assertThrows(NutritionPlanNotFoundException.class, () ->
                nutritionPlanService.updatePlan(99L, new CreateNutritionPlanRequest(
                        "X", null, null, 0.0, PlanStatus.DRAFT)));
        assertEquals(ApplicationConstants.NUTRITION_PLAN_NOT_FOUND + 99L, ex.getMessage());
    }

    @Test
    void deletePlan_deletesWhenExists() {
        when(planRepo.existsById(3L)).thenReturn(true);

        nutritionPlanService.deletePlan(3L);

        verify(planRepo).deleteById(3L);
    }

    @Test
    void deletePlan_throwsWhenMissing() {
        when(planRepo.existsById(4L)).thenReturn(false);

        NutritionPlanNotFoundException ex = assertThrows(NutritionPlanNotFoundException.class, () ->
                nutritionPlanService.deletePlan(4L));
        assertEquals(ApplicationConstants.NUTRITION_PLAN_NOT_FOUND + 4L, ex.getMessage());
    }

    @Test
    void savePlan_nullId_delegatesToCreate() {
        CreateNutritionPlanRequest request = new CreateNutritionPlanRequest(
                "New", null, null, 10.0, PlanStatus.DRAFT);
        NutritionPlan entity = new NutritionPlan();
        NutritionPlan saved = new NutritionPlan();
        saved.setId(1L);
        NutritionPlanCatalogItemDto dto = new NutritionPlanCatalogItemDto();
        dto.setId(1L);

        when(mapper.toEntity(request)).thenReturn(entity);
        when(planRepo.save(entity)).thenReturn(saved);
        when(mapper.toCatalogItem(saved)).thenReturn(dto);

        NutritionPlanService service = nutritionPlanService;
        NutritionPlanCatalogItemDto result = service.savePlan(null, request);

        assertEquals(1L, result.getId());
    }

    @Test
    void savePlan_withId_delegatesToUpdate() {
        Long id = 2L;
        CreateNutritionPlanRequest request = new CreateNutritionPlanRequest(
                "U", "c", "d", 5.0, PlanStatus.DRAFT);
        NutritionPlan existing = new NutritionPlan();
        existing.setId(id);
        when(planRepo.findById(id)).thenReturn(Optional.of(existing));
        when(planRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        NutritionPlanCatalogItemDto dto = new NutritionPlanCatalogItemDto();
        dto.setId(id);
        when(mapper.toCatalogItem(any())).thenReturn(dto);

        NutritionPlanService service = nutritionPlanService;
        NutritionPlanCatalogItemDto result = service.savePlan(id, request);

        assertEquals(id, result.getId());
    }
}
