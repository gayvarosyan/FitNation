package com.example.fitnationrestapi.validation;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationrestapi.exception.EmailAlreadyExistsException;
import com.example.fitnationrestapi.exception.InvalidPasswordException;
import com.example.fitnationrestapi.exception.InvalidRoleException;
import com.example.fitnationrestapi.exception.UserNotFoundException;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidations {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void validateEmailNotTaken(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }

    public void validateRole(UserRole role) {
        if (role != UserRole.CLIENT && role != UserRole.TRAINER) {
            throw new InvalidRoleException("Invalid role");
        }
    }

    public User validateUserExists(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidPasswordException("Invalid password");
        }
    }
}