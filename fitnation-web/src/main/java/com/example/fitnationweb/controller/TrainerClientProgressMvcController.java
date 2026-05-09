package com.example.fitnationweb.controller;

import com.example.fitnationweb.service.MvcTrainerClientProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/trainers/clients/{userId}/progress")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TRAINER')")
public class TrainerClientProgressMvcController {

    private static final String PROGRESS_PATH = "/trainers/clients";

    private final MvcTrainerClientProgressService mvcTrainerClientProgressService;

    @GetMapping
    public String progressHistory(@PathVariable Long userId, Model model) {
        try {
            mvcTrainerClientProgressService.populateHistoryModel(userId, model);
            return "trainer/client-progress";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:" + PROGRESS_PATH;
        }
    }

    @GetMapping("/summary")
    public String progressSummary(@PathVariable Long userId, Model model) {
        try {
            mvcTrainerClientProgressService.populateSummaryModel(userId, model);
            return "trainer/client-progress-summary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:" + PROGRESS_PATH + "/" + userId + "/progress";
        }
    }
}
