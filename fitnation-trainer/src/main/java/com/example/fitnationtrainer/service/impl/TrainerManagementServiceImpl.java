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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerManagementServiceImpl implements TrainerManagementService {

    private static final Logger log = LoggerFactory.getLogger(TrainerManagementServiceImpl.class);

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainerMapper trainerMapper;

    @Override
    @Transactional(readOnly = true)
    public TrainerStatsResponse getStats() {
        long totalTrainers = trainerRepository.count();
        long currentlyActive = userRepository.countByRoleAndStatus(UserRole.TRAINER, UserStatus.ACTIVE);
        log.debug("getStats: totalTrainers={}, currentlyActive={}", totalTrainers, currentlyActive);
        return new TrainerStatsResponse(totalTrainers, currentlyActive);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerDirectoryItem> getDirectory() {
        List<TrainerDirectoryItem> items = trainerRepository.findAll().stream()
                .map(trainerMapper::toDirectoryItem)
                .toList();
        log.debug("getDirectory: returned {} trainer(s)", items.size());
        return items;
    }

    @Override
    @Transactional
    public TrainerDirectoryItem create(CreateTrainerRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("create trainer failed: email already exists, email={}", request.email());
            throw new EmailAlreadyExistsException("Email already exists");
        }
        log.info("Creating trainer: email={}, firstName={}, lastName={}", request.email(), request.firstName(), request.lastName());
        Trainer trainer = trainerMapper.toTrainer(request);
        trainer = trainerRepository.save(trainer);
        log.info("Trainer created: id={}, email={}", trainer.getId(), trainer.getEmail());
        return trainerMapper.toDirectoryItem(trainer);
    }

    @Override
    @Transactional
    public TrainerDirectoryItem edit(Long id, EditTrainerRequest request) {
        log.debug("Editing trainer: id={}", id);
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("edit trainer failed: trainer not found, id={}", id);
                    return new TrainerNotFoundException("Trainer not found: " + id);
                });

        trainerMapper.updateTrainer(trainer, request);

        trainer = trainerRepository.save(trainer);
        log.info("Trainer updated: id={}, email={}", trainer.getId(), trainer.getEmail());
        return trainerMapper.toDirectoryItem(trainer);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting trainer: id={}", id);
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("delete trainer failed: trainer not found, id={}", id);
                    return new TrainerNotFoundException("Trainer not found: " + id);
                });
        trainerRepository.delete(trainer);
        userRepository.delete(trainer);
        log.info("Trainer deleted: id={}, email={}", trainer.getId(), trainer.getEmail());
    }
}
