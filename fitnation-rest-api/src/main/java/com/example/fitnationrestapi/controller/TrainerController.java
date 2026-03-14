package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerStatsResponse;
import com.example.fitnationtrainer.service.TrainerManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class TrainerController {

    private final TrainerManagementService trainerManagementService;

    @GetMapping("/stats")
    public TrainerStatsResponse getStats() {
        return trainerManagementService.getStats();
    }

    @GetMapping
    public List<TrainerDirectoryItem> getDirectory() {
        return trainerManagementService.getDirectory();
    }

    @PostMapping
    public TrainerDirectoryItem create(@Valid @RequestBody CreateTrainerRequest request) {
        return trainerManagementService.create(request);
    }

    @PutMapping("/{id}")
    public TrainerDirectoryItem edit(
            @PathVariable Long id,
            @Valid @RequestBody EditTrainerRequest request) {
        return trainerManagementService.edit(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        trainerManagementService.delete(id);
    }
}
