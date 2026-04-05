package com.example.fitnationcommon.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static com.example.fitnationcommon.constants.ApplicationConstants.EMAIL_REQUIRED;
import static com.example.fitnationcommon.constants.ApplicationConstants.EMAIL_VALID;
import static com.example.fitnationcommon.constants.ApplicationConstants.FIRST_NAME_MAX_SIZE;
import static com.example.fitnationcommon.constants.ApplicationConstants.FIRST_NAME_REQUIRED;
import static com.example.fitnationcommon.constants.ApplicationConstants.LAST_NAME_MAX_SIZE;
import static com.example.fitnationcommon.constants.ApplicationConstants.LAST_NAME_REQUIRED;
import static com.example.fitnationcommon.constants.ApplicationConstants.NAME_MAX_SIZE;
import static com.example.fitnationcommon.constants.ApplicationConstants.PASSWORD_REQUIRED;
import static com.example.fitnationcommon.constants.ApplicationConstants.PHONE_MAX_LENGTH;
import static com.example.fitnationcommon.constants.ApplicationConstants.PHONE_MAX_SIZE;
import static com.example.fitnationcommon.constants.ApplicationConstants.PHONE_REQUIRED;

@Data
public class CreateMemberRequest {

    @NotBlank(message = FIRST_NAME_REQUIRED)
    @Size(max = NAME_MAX_SIZE, message = FIRST_NAME_MAX_SIZE)
    private String firstName;

    @NotBlank(message = LAST_NAME_REQUIRED)
    @Size(max = NAME_MAX_SIZE, message = LAST_NAME_MAX_SIZE)
    private String lastName;

    @NotBlank(message = EMAIL_REQUIRED)
    @Email(message = EMAIL_VALID)
    private String email;

    @NotBlank(message = PHONE_REQUIRED)
    @Size(max = PHONE_MAX_LENGTH, message = PHONE_MAX_SIZE)
    private String phone;

    @NotBlank(message = PASSWORD_REQUIRED)
    private String password;

    private Long assignedTrainerId;

    private Long assignedNutritionPlanId;
}
