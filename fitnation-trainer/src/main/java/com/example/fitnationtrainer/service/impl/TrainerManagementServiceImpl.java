package com.example.fitnationtrainer.service.impl;

import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerStatsResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.EmailAlreadyExistsException;
import com.example.fitnationcommon.exception.TrainerNotFoundException;
import com.example.fitnationtrainer.entity.Trainer;
import com.example.fitnationtrainer.mapper.TrainerMapper;
import com.example.fitnationtrainer.repository.TrainerRepository;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerManagementServiceImpl implements TrainerManagementService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainerMapper trainerMapper;

    @Override
    @Transactional(readOnly = true)
    public TrainerStatsResponse getStats() {
        long totalTrainers = trainerRepository.count();
        long currentlyActive = userRepository.countByRoleAndStatus(UserRole.TRAINER, UserStatus.ACTIVE);
        return new TrainerStatsResponse(totalTrainers, currentlyActive);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerDirectoryItem> getDirectory() {
        return trainerRepository.findAll().stream()
                .map(trainerMapper::toDirectoryItem)
                .toList();
    }

    @Override
    @Transactional
    public TrainerDirectoryItem create(CreateTrainerRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        Trainer trainer = trainerMapper.toTrainer(request);
        trainer = trainerRepository.save(trainer);
        return trainerMapper.toDirectoryItem(trainer);
    }

    @Override
    @Transactional
    public TrainerDirectoryItem edit(Long id, EditTrainerRequest request) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + id));

        trainerMapper.updateTrainer(trainer, request);

        return trainerMapper.toDirectoryItem(trainerRepository.save(trainer));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + id));
        trainerRepository.delete(trainer);
        userRepository.delete(trainer);
    }
}
