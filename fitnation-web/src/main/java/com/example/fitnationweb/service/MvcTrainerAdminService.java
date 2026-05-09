package com.example.fitnationweb.service;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class MvcTrainerAdminService {

    private static final String PAGE = "/admin/trainers";

    private final GroupClassService groupClassService;
    private final TrainerManagementService trainerManagementService;

    public void populatePageModel(Model model) {
        model.addAttribute("stats", trainerManagementService.getStats());
        model.addAttribute("trainers", trainerManagementService.getDirectory(0, 100, null, null, null).getItems());
        model.addAttribute("navSection", "trainers");
    }

    public MvcRedirect createTrainer(CreateTrainerRequest request) {
        try {
            trainerManagementService.create(request);
            return MvcRedirect.to(PAGE, "Trainer added.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE, e.getMessage());
        }
    }

    public MvcRedirect updateTrainer(
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
                    firstName == null ? null : firstName.trim(),
                    lastName == null ? null : lastName.trim(),
                    pwd,
                    phone == null ? null : phone.trim(),
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

    public MvcRedirect deleteTrainer(Long trainerId) {
        try {
            groupClassService.deleteAllByTrainerId(trainerId);
            trainerManagementService.delete(trainerId);
            return MvcRedirect.to(PAGE, "Trainer deleted.");
        } catch (Exception e) {
            return MvcRedirect.failure(PAGE, e.getMessage());
        }
    }
}

