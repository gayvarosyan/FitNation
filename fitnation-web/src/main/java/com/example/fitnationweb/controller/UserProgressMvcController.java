package com.example.fitnationweb.controller;

import com.example.fitnationprogress.dto.UpsertUserProgressEntryRequest;
import com.example.fitnationweb.service.MvcUserProgressService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users/progress")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class UserProgressMvcController {

    private static final String PROGRESS_PATH = "/users/progress";

    private final MvcUserProgressService mvcUserProgressService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping
    public String progressHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        try {
            Long userId = currentUserAccessor.requireUser().getId();
            mvcUserProgressService.populateHistoryModel(userId, model);
            return "user/progress-history";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/progress-history";
        }
    }

    @GetMapping("/{id}")
    public String getProgressEntry(@PathVariable Long id, Model model) {
        try {
            mvcUserProgressService.populateEntryModel(currentUserAccessor.requireUser().getId(), id, model);
            return "user/progress-entry";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:" + PROGRESS_PATH;
        }
    }

    @GetMapping("/summary")
    public String progressSummary(Model model) {
        try {
            mvcUserProgressService.populateSummaryModel(currentUserAccessor.requireUser().getId(), model);
            return "user/progress-summary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/progress-summary";
        }
    }

    @PostMapping
    public String createProgressEntry(@Valid UpsertUserProgressEntryRequest request, RedirectAttributes redirectAttributes) {
        var result = mvcUserProgressService.createEntry(currentUserAccessor.requireUser().getId(), request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PutMapping("/{id}")
    public String updateProgressEntry(
            @PathVariable Long id,
            @Valid UpsertUserProgressEntryRequest request,
            RedirectAttributes redirectAttributes) {
        var result = mvcUserProgressService.updateEntry(currentUserAccessor.requireUser().getId(), id, request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/{id}/delete")
    public String deleteProgressEntry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var result = mvcUserProgressService.deleteEntry(currentUserAccessor.requireUser().getId(), id);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
