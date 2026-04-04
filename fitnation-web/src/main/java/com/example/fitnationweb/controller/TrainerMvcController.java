package com.example.fitnationweb.controller;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationweb.support.MvcRedirect;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/trainers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class TrainerMvcController {

    private static final String PAGE = "/admin/trainers";

    private final GroupClassService groupClassService;
    private final TrainerManagementService trainerManagementService;

    @GetMapping
    public String page(Model model) {
        model.addAttribute("stats", trainerManagementService.getStats());
        model.addAttribute("trainers", trainerManagementService.getDirectory());
        model.addAttribute("navSection", "trainers");
        return "admin/trainers";
    }

    @PostMapping("/create")
    public String create(@Valid CreateTrainerRequest request, RedirectAttributes redirectAttributes) {
        var result = createTrainer(request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/edit")
    public String edit(
            @RequestParam Long trainerId,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String password,
            @RequestParam String phone,
            @RequestParam UserStatus status,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String bio,
            RedirectAttributes redirectAttributes) {
        var result = updateTrainer(trainerId, firstName, lastName, password, phone, status, specialization, bio);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long trainerId, RedirectAttributes redirectAttributes) {
        var result = deleteTrainer(trainerId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    private MvcRedirect createTrainer(CreateTrainerRequest request) {
        try {
            trainerManagementService.create(request);
            return MvcRedirect.to(PAGE, "Trainer added.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE, e.getMessage());
        }
    }

    private MvcRedirect updateTrainer(
            Long trainerId,
            String firstName,
            String lastName,
            String password,
            String phone,
            UserStatus status,
            String specialization,
            String bio) {
        try {
            String pwd = password != null && !password.isBlank() ? password : null;
            EditTrainerRequest request = new EditTrainerRequest(
                    firstName.trim(),
                    lastName.trim(),
                    pwd,
                    phone.trim(),
                    specialization != null ? specialization.trim() : null,
                    bio != null ? bio.trim() : null,
                    status
            );
            trainerManagementService.edit(trainerId, request);
            return MvcRedirect.to(PAGE, "Trainer updated.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE, e.getMessage());
        }
    }

    private MvcRedirect deleteTrainer(Long trainerId) {
        try {
            groupClassService.deleteAllByTrainerId(trainerId);
            trainerManagementService.delete(trainerId);
            return MvcRedirect.to(PAGE, "Trainer deleted.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE, e.getMessage());
        }
    }
}
