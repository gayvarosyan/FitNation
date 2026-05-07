package com.example.fitnationweb.controller;

import com.example.fitnationweb.controller.dto.ProgressEntryRequest;
import com.example.fitnationweb.service.MvcProgressService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequiredArgsConstructor
@Slf4j
public class ProgressMvcController {

    private final MvcProgressService mvcProgressService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping("/portal/progress")
    @PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
    public String myProgress(Model model) {
        var user = currentUserAccessor.requireUser();
        mvcProgressService.populatePortalProgressModel(user.getId(), model);
        return "portal/progress";
    }

    @PostMapping("/portal/progress/create")
    @PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
    public String create(
            @Valid @ModelAttribute("progressEntry") ProgressEntryRequest progressEntryRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", firstValidationError(bindingResult));
            return "redirect:/portal/progress";
        }
        Long userId = currentUserId();
        var result = mvcProgressService.createPortalEntry(
                userId,
                progressEntryRequest.recordedAt(),
                progressEntryRequest.weight(),
                progressEntryRequest.bodyFatPercent(),
                progressEntryRequest.muscleMass(),
                progressEntryRequest.waistCm(),
                progressEntryRequest.chestCm(),
                progressEntryRequest.hipCm(),
                progressEntryRequest.notes());
        if (result.error() != null) {
            log.warn("Create progress entry failed for userId={}: {}", userId, result.error());
        }
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/portal/progress/{entryId}/update")
    @PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
    public String update(
            @PathVariable Long entryId,
            @Valid @ModelAttribute("progressEntry") ProgressEntryRequest progressEntryRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", firstValidationError(bindingResult));
            return "redirect:/portal/progress";
        }
        Long userId = currentUserId();
        var result = mvcProgressService.updatePortalEntry(
                userId,
                entryId,
                progressEntryRequest.recordedAt(),
                progressEntryRequest.weight(),
                progressEntryRequest.bodyFatPercent(),
                progressEntryRequest.muscleMass(),
                progressEntryRequest.waistCm(),
                progressEntryRequest.chestCm(),
                progressEntryRequest.hipCm(),
                progressEntryRequest.notes());
        if (result.error() != null) {
            log.warn("Update progress entry failed for userId={}, entryId={}: {}", userId, entryId, result.error());
        }
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/portal/progress/{entryId}/delete")
    @PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
    public String delete(@PathVariable Long entryId, RedirectAttributes redirectAttributes) {
        var result = mvcProgressService.deletePortalEntry(currentUserId(), entryId);
        if (result.error() != null) {
            log.warn("Delete progress entry failed for entryId={}: {}", entryId, result.error());
        }
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @GetMapping("/portal/trainer/clients/{userId}/progress")
    @PreAuthorize("hasRole('TRAINER')")
    public String trainerClientProgress(@PathVariable Long userId, Model model) {
        var trainer = currentUserAccessor.requireUser();
        mvcProgressService.populateClientProgressModel(trainer.getId(), trainer.getRole(), userId, "progress", model);
        return "portal/trainer-client-progress";
    }

    private Long currentUserId() {
        return currentUserAccessor.requireUser().getId();
    }

    private static String firstValidationError(BindingResult bindingResult) {
        var error = bindingResult.getFieldError();
        if (error != null && error.getDefaultMessage() != null) {
            return error.getDefaultMessage();
        }
        return "Please check your input.";
    }
}
