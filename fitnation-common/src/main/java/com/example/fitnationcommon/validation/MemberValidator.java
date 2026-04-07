package com.example.fitnationcommon.validation;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static com.example.fitnationcommon.constants.ApplicationConstants.NAME_INVALID_CHARS;
import static com.example.fitnationcommon.constants.ApplicationConstants.NAME_MAX_SIZE_EXCEEDED;
import static com.example.fitnationcommon.constants.ApplicationConstants.NAME_REQUIRED;
import static com.example.fitnationcommon.constants.ApplicationConstants.PASSWORD_INVALID;
import static com.example.fitnationcommon.constants.ApplicationConstants.PASSWORD_REGEX;
import static com.example.fitnationcommon.constants.ApplicationConstants.PASSWORD_REQUIRED;

@Component
@Slf4j
public class MemberValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(ApplicationConstants.EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");

    public void validateCreateMemberRequest(CreateMemberRequest request) {
        validateName(request.getFirstName(), "First name");
        validateName(request.getLastName(), "Last name");
        validateEmail(request.getEmail());
        validatePhone(request.getPhone());
        validatePassword(request.getPassword());
    }

    public void validateUpdateMemberRequest(UpdateMemberRequest request) {
        if (request.getFirstName() != null) {
            validateName(request.getFirstName(), "First name");
        }
        if (request.getLastName() != null) {
            validateName(request.getLastName(), "Last name");
        }
        if (request.getEmail() != null) {
            validateEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            validatePhone(request.getPhone());
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            validatePassword(request.getPassword());
        }
    }

    private void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException(fieldName + NAME_REQUIRED);
        }
        if (name.length() > ApplicationConstants.NAME_MAX_SIZE) {
            throw new ValidationException(fieldName + NAME_MAX_SIZE_EXCEEDED);
        }
        if (!name.matches("^[a-zA-Z\\s'-]+$")) {
            throw new ValidationException(fieldName + NAME_INVALID_CHARS);
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(ApplicationConstants.EMAIL_REQUIRED);
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(ApplicationConstants.VALID_EMAIL_MESSAGE);
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException(ApplicationConstants.PHONE_REQUIRED);
        }
        if (!PHONE_PATTERN.matcher(phone.replaceAll("[\\s\\-()]", "")).matches()) {
            throw new IllegalArgumentException(ApplicationConstants.PHONE_INVALID_FORMAT);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException(PASSWORD_REQUIRED);
        }
        if (!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new ValidationException(PASSWORD_INVALID);
        }
    }
}
