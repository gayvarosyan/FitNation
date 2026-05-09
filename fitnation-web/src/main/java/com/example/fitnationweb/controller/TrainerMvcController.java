package com.example.fitnationweb.controller;

import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationweb.service.MvcTrainerAdminService;
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
@PreAuthorize("hasRole('ADMIN')")
public class TrainerMvcController {

    private static final String PAGE = "/admin/trainers";

    private final MvcTrainerAdminService mvcTrainerAdminService;

    @GetMapping
    public String page(Model model) {
        mvcTrainerAdminService.populatePageModel(model);
        return "admin/trainers";
    }

    @PostMapping("/create")
    public String create(@Valid CreateTrainerRequest request, RedirectAttributes redirectAttributes) {
        var result = mvcTrainerAdminService.createTrainer(request);
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
        var result = mvcTrainerAdminService.updateTrainer(
                trainerId, firstName, lastName, password, phone, status, specialization, bio);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long trainerId, RedirectAttributes redirectAttributes) {
        var result = mvcTrainerAdminService.deleteTrainer(trainerId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
