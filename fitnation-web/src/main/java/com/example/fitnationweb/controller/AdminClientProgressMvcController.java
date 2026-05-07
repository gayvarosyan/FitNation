package com.example.fitnationweb.controller;

import com.example.fitnationweb.service.MvcProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/clients/{userId}/progress")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminClientProgressMvcController {

    private static final String PROGRESS_PATH = "/admin/clients";

    private final MvcProgressService mvcProgressService;

    @GetMapping
    public String progressHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        try {
            mvcProgressService.populateAdminClientProgressModel(userId, model);
            return "admin/client-progress";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:" + PROGRESS_PATH;
        }
    }

    @GetMapping("/summary")
    public String progressSummary(@PathVariable Long userId, Model model) {
        try {
            mvcProgressService.populateAdminClientProgressSummaryModel(userId, model);
            return "admin/client-progress-summary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:" + PROGRESS_PATH + "/" + userId + "/progress";
        }
    }
}
