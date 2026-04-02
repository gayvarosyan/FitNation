package com.example.fitnationcommon.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberListResponse {

    private Long id;
    private String formattedId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String planName;
    private String membershipStatusLabel;
    private LocalDateTime joinDate;
    private String userStatus;
    private String trainerName;
    private String nutritionPlanName;
}
