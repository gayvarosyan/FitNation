package com.example.fitnationrestapi.service;

import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.dto.response.AuthResponse;
import com.example.fitnationrestapi.Mapper.UserMapper;
import com.example.fitnationrestapi.validation.AuthValidations;
import com.example.fitnationtainuser.repository.TrainerRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final TrainerRepository trainerRepo;
    private final UserMapper userMapper;
    private final AuthValidations authValidations;

    public AuthResponse register(RegisterRequest request) {
        authValidations.validateEmailNotTaken(request.email());
        authValidations.validateRole(request.role());

        if (request.role() == UserRole.CLIENT) {
            userRepo.save(userMapper.toUser(request));
        } else {
            trainerRepo.save(userMapper.toTrainer(request));
        }
        return null;
    }

    public AuthResponse login(String email, String rawPassword) {
        User user = authValidations.validateUserExists(email);
        authValidations.validatePassword(rawPassword, user.getPassword());

        user.setStatus(UserStatus.ACTIVE);
        userRepo.save(user);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name()
        );
    }
}