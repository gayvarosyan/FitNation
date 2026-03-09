package com.example.fitnationtrainer.service.impl;

import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.exception.EmailAlreadyExistsException;
import com.example.fitnationtrainer.entity.Trainer;
import com.example.fitnationtrainer.mapper.TrainerMapper;
import com.example.fitnationtrainer.repository.TrainerRepository;
import com.example.fitnationtrainer.service.TrainerRegistrationService;
import com.example.fitnationuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerRegistrationServiceImpl implements TrainerRegistrationService {

    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;

    @Override
    public Trainer register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        return trainerRepository.save(trainerMapper.toTrainer(request));
    }
}
