package com.example.fitnationrestapi.service;

import com.example.fitnationcommon.dto.RegisterRequest;
import com.example.fitnationrestapi.Mapper.UserMapper;
import com.example.fitnationtainuser.entity.Trainer;
import com.example.fitnationtainuser.repository.TrainerRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final TrainerRepository trainerRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {

        if (userRepo.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (request.role() == UserRole.CLIENT) {
            User user = userMapper.toUser(request);
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setRole(UserRole.CLIENT);
            user.setStatus(UserStatus.INACTIVE);

            userRepo.save(user);

        } else if (request.role() == UserRole.TRAINER) {
            Trainer trainer = userMapper.toTrainer(request);
            trainer.setPassword(passwordEncoder.encode(request.password()));
            trainer.setRole(UserRole.TRAINER);
            trainer.setStatus(UserStatus.INACTIVE);

            trainerRepo.save(trainer);

        } else {
            throw new RuntimeException("Invalid role");
        }
    }

    public User login(String email, String rawPassword) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepo.save(user);

        return user;
    }
}