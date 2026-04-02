package com.example.fitnationcommon.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberDetailResponse {

    private Long id;
    private String formattedId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String userStatus;
    private LocalDateTime joinDate;
    private LocalDateTime updatedAt;
    
    private MembershipSummary currentMembership;
    private TrainerSummary assignedTrainer;
    private NutritionPlanSummary assignedNutritionPlan;
    
    @Data
    public static class MembershipSummary {
        private Long membershipId;
        private String planName;
        private String status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Boolean isActive;
    }
    
    @Data
    public static class TrainerSummary {
        private Long trainerId;
        private String fullName;
        private String specialization;
        private String email;
    }
    
    @Data
    public static class NutritionPlanSummary {
        private Long planId;
        private String planName;
        private String category;
        private String description;
    }
}
