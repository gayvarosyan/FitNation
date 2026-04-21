
package com.example.fitnationtrainer.service.impl;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerStatsResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.EmailAlreadyExistsException;
import com.example.fitnationcommon.exception.TrainerNotFoundException;
import com.example.fitnationcommon.exception.UserPendingException;
import com.example.fitnationtrainer.entity.Trainer;
import com.example.fitnationtrainer.mapper.TrainerMapper;
import com.example.fitnationtrainer.repository.TrainerRepository;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationcommon.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerManagementServiceImpl implements TrainerManagementService {

    private static final Logger log = LoggerFactory.getLogger(TrainerManagementServiceImpl.class);

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainerMapper trainerMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${fitnation.app.login-url:http://localhost:8080/login}")
    private String loginUrl;

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
        List<TrainerDirectoryItem> items = trainerRepository.findAllByDeletedAtIsNull().stream()
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
            throw new EmailAlreadyExistsException(ApplicationConstants.EMAIL_ALREADY_EXISTS);
        }
        log.info("Creating trainer: email={}, firstName={}, lastName={}", request.email(), request.firstName(), request.lastName());
        String adminPassword = request.password();
        Trainer trainer = trainerMapper.toTrainer(request);
        trainer.setPassword(passwordEncoder.encode(adminPassword));
        trainer.setRole(UserRole.TRAINER);
        trainer.setStatus(UserStatus.PENDING);
        trainer = trainerRepository.save(trainer);
        log.info(
                ApplicationConstants.LOG_TRAINER_CREATED,
                trainer.getId(),
                trainer.getEmail(),
                trainer.getStatus());

        if (emailService.sendInvitationEmail(trainer.getEmail(), adminPassword, loginUrl)) {
            log.info(ApplicationConstants.LOG_TRAINER_INVITATION_EMAIL_SENT, trainer.getEmail());
        }

        return trainerMapper.toDirectoryItem(trainer);
    }

    @Override
    @Transactional
    public TrainerDirectoryItem edit(Long id, EditTrainerRequest request) {
        log.debug("Editing trainer: id={}", id);
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("edit trainer failed: trainer not found, id={}", id);
                    return new TrainerNotFoundException(ApplicationConstants.MSG_TRAINER_NOT_FOUND + id);
                });

        if (trainer.getStatus() == UserStatus.PENDING) {
            log.warn("edit trainer failed: user is PENDING, id={}", id);
            throw new UserPendingException(ApplicationConstants.USER_PENDING_CANNOT_EDIT);
        }

        trainerMapper.updateTrainer(trainer, request);

        trainer = trainerRepository.save(trainer);
        log.info("Trainer updated: id={}, email={}", trainer.getId(), trainer.getEmail());
        return trainerMapper.toDirectoryItem(trainer);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Soft deleting trainer: id={}", id);
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("delete trainer failed: trainer not found, id={}", id);
                    return new TrainerNotFoundException(ApplicationConstants.MSG_TRAINER_NOT_FOUND + id);
                });

        trainer.setDeletedAt(LocalDateTime.now());
        trainer.setStatus(UserStatus.DELETED);
        trainerRepository.save(trainer);

        log.info("Trainer soft deleted: id={}, email={}", trainer.getId(), trainer.getEmail());
    }
}