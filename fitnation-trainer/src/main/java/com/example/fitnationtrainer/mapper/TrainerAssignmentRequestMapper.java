package com.example.fitnationtrainer.mapper;

import com.example.fitnationcommon.dto.response.TrainerAssignmentRequestResponse;
import com.example.fitnationcommon.dto.response.TrainerPublicProfileResponse;
import com.example.fitnationcommon.enums.TrainerAssignmentRequestStatus;
import com.example.fitnationtrainer.entity.Trainer;
import com.example.fitnationtrainer.entity.TrainerAssignmentRequest;
import com.example.fitnationuser.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainerAssignmentRequestMapper {

    public TrainerAssignmentRequestResponse toResponse(TrainerAssignmentRequest request) {
        if (request == null) return null;

        User client = request.getClient();
        Trainer trainer = request.getTrainer();

        return TrainerAssignmentRequestResponse.builder()
                .id(request.getId())
                .clientUserId(client.getId())
                .clientFullName(client.getFirstName() + " " + client.getLastName())
                .trainerUserId(trainer.getId())
                .trainerFullName(trainer.getFirstName() + " " + trainer.getLastName())
                .status(request.getStatus())
                .message(request.getMessage())
                .createdAt(request.getCreatedAt())
                .resolvedAt(request.getResolvedAt())
                .build();
    }

    public List<TrainerAssignmentRequestResponse> toResponseList(List<TrainerAssignmentRequest> requests) {
        return requests.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TrainerPublicProfileResponse toPublicProfileResponse(Trainer trainer) {
        if (trainer == null) return null;

        return TrainerPublicProfileResponse.builder()
                .userId(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .specialization(trainer.getSpecialization())
                .bio(trainer.getBio())
                .canRequest(false)
                .existingRequestStatus(null)
                .build();
    }

    public void enrichWithClientContext(
            TrainerPublicProfileResponse dto,
            boolean canRequest,
            TrainerAssignmentRequestStatus existingRequestStatus) {

        dto.setCanRequest(canRequest);
        dto.setExistingRequestStatus(existingRequestStatus);
    }
}