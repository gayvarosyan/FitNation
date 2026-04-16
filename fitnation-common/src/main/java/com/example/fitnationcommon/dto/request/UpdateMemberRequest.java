package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static com.example.fitnationcommon.constants.ApplicationConstants.EMAIL_VALID;
import static com.example.fitnationcommon.constants.ApplicationConstants.FIRST_NAME_MAX_SIZE;
import static com.example.fitnationcommon.constants.ApplicationConstants.LAST_NAME_MAX_SIZE;
import static com.example.fitnationcommon.constants.ApplicationConstants.NAME_MAX_SIZE;
import static com.example.fitnationcommon.constants.ApplicationConstants.PHONE_MAX_LENGTH;
import static com.example.fitnationcommon.constants.ApplicationConstants.PHONE_MAX_SIZE;

@Data
public class UpdateMemberRequest {

    @Size(max = NAME_MAX_SIZE, message = FIRST_NAME_MAX_SIZE)
    private String firstName;

    @Size(max = NAME_MAX_SIZE, message = LAST_NAME_MAX_SIZE)
    private String lastName;

    @Email(message = EMAIL_VALID)
    private String email;

    @Size(max = PHONE_MAX_LENGTH, message = PHONE_MAX_SIZE)
    private String phone;

    private String password;

    private Long assignedTrainerId;

    private Long assignedNutritionPlanId;

    private UserStatus status;
}
