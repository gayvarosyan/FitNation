package com.example.fitnationweb.controller;

import com.example.fitnationcommon.dto.request.ApproveRejectTrainerRequestRequest;
import com.example.fitnationcommon.dto.request.CreateTrainerAssignmentRequest;
import com.example.fitnationweb.service.MvcTrainerAssignmentService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/trainer-assignments")
@RequiredArgsConstructor
public class TrainerAssignmentMvcController {


    private final MvcTrainerAssignmentService mvcTrainerAssignmentService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping("/trainers")
    @PreAuthorize("hasRole('CLIENT')")
    public String getActiveTrainers(Model model) {
        try {
            mvcTrainerAssignmentService.populateActiveTrainersModel(model);
            return "client/trainer-directory";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/trainer-directory";
        }
    }

    @GetMapping("/trainers/{trainerId}")
    @PreAuthorize("hasRole('CLIENT')")
    public String getTrainerProfile(@PathVariable Long trainerId, Model model) {
        try {
            Long clientId = currentUserAccessor.requireUser().getId();
            mvcTrainerAssignmentService.populateTrainerProfileModel(trainerId, clientId, model);
            return "client/trainer-profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/trainer-assignments/trainers";
        }
    }

    @PostMapping("/request")
    @PreAuthorize("hasRole('CLIENT')")
    public String requestTrainer(@Valid CreateTrainerAssignmentRequest request, RedirectAttributes redirectAttributes) {
        Long clientId = currentUserAccessor.requireUser().getId();
        var result = mvcTrainerAssignmentService.requestTrainer(clientId, request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('CLIENT')")
    public String getClientRequests(Model model) {
        try {
            Long clientId = currentUserAccessor.requireUser().getId();
            mvcTrainerAssignmentService.populateClientRequestsModel(clientId, model);
            return "client/my-assignment-requests";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/my-assignment-requests";
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('TRAINER')")
    public String getPendingRequests(Model model) {
        try {
            Long trainerId = currentUserAccessor.requireUser().getId();
            mvcTrainerAssignmentService.populatePendingRequestsModel(trainerId, model);
            return "trainer/pending-assignments";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "trainer/pending-assignments";
        }
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('TRAINER')")
    public String approveRequest(@Valid ApproveRejectTrainerRequestRequest request, RedirectAttributes redirectAttributes) {
        Long trainerId = currentUserAccessor.requireUser().getId();
        var result = mvcTrainerAssignmentService.approve(trainerId, request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/reject")
    @PreAuthorize("hasRole('TRAINER')")
    public String rejectRequest(@Valid ApproveRejectTrainerRequestRequest request, RedirectAttributes redirectAttributes) {
        Long trainerId = currentUserAccessor.requireUser().getId();
        var result = mvcTrainerAssignmentService.reject(trainerId, request);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
