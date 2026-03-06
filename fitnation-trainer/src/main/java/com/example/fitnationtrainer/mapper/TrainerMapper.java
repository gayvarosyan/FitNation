package com.example.fitnationtrainer.mapper;

import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationtrainer.entity.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerMapper {

    private final PasswordEncoder passwordEncoder;

    public Trainer toTrainer(RegisterRequest request) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(request.firstName());
        trainer.setLastName(request.lastName());
        trainer.setEmail(request.email());
        trainer.setPhone(request.phone());
        trainer.setPassword(passwordEncoder.encode(request.password()));
        trainer.setRole(UserRole.TRAINER);
        trainer.setStatus(UserStatus.INACTIVE);
        trainer.setSpecialization(request.specialization() != null ? request.specialization() : "");
        trainer.setBio(request.bio());
        return trainer;
    }

    public Trainer toTrainer(CreateTrainerRequest request) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(request.firstName());
        trainer.setLastName(request.lastName());
        trainer.setEmail(request.email());
        trainer.setPhone(request.phone());
        trainer.setPassword(passwordEncoder.encode(request.password()));
        trainer.setRole(UserRole.TRAINER);
        trainer.setStatus(UserStatus.ACTIVE);
        trainer.setSpecialization(request.specialization() != null ? request.specialization() : "");
        trainer.setBio(request.bio() != null ? request.bio() : "");
        return trainer;
    }

    public TrainerDirectoryItem toDirectoryItem(Trainer trainer) {
        return new TrainerDirectoryItem(
                String.valueOf(trainer.getId()),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getSpecialization() != null ? trainer.getSpecialization() : "",
                trainer.getBio() != null ? trainer.getBio() : "",
                trainer.getEmail(),
                trainer.getPhone() != null ? trainer.getPhone() : "",
                trainer.getStatus()
        );
    }
}
