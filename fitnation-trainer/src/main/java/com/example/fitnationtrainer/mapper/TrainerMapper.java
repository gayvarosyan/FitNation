package com.example.fitnationtrainer.mapper;

import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
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
        Trainer trainer = baseTrainer(request.firstName(), request.lastName(), request.email(), request.phone());
        trainer.setPassword(passwordEncoder.encode(request.password()));
        trainer.setStatus(UserStatus.INACTIVE);
        trainer.setSpecialization(blankToEmpty(request.specialization()));
        trainer.setBio(request.bio());
        return trainer;
    }

    public Trainer toTrainer(CreateTrainerRequest request) {
        Trainer trainer = baseTrainer(request.firstName(), request.lastName(), request.email(), request.phone());
        trainer.setPassword(passwordEncoder.encode(request.password()));
        trainer.setStatus(UserStatus.ACTIVE);
        trainer.setSpecialization(blankToEmpty(request.specialization()));
        trainer.setBio(blankToEmpty(request.bio()));
        return trainer;
    }

    public void updateTrainer(Trainer trainer, EditTrainerRequest request) {
        trainer.setFirstName(request.firstName());
        trainer.setLastName(request.lastName());
        if (request.password() != null) {
            trainer.setPassword(passwordEncoder.encode(request.password()));
        }
        trainer.setPhone(request.phone());
        trainer.setSpecialization(request.specialization());
        trainer.setBio(request.bio());
        if (request.status() != null) {
            trainer.setStatus(request.status());
        }
    }

    public TrainerDirectoryItem toDirectoryItem(Trainer trainer) {
        return new TrainerDirectoryItem(
                String.valueOf(trainer.getId()),
                trainer.getFirstName(),
                trainer.getLastName(),
                blankToEmpty(trainer.getSpecialization()),
                blankToEmpty(trainer.getBio()),
                trainer.getEmail(),
                blankToEmpty(trainer.getPhone()),
                trainer.getStatus(),
                false
        );
    }

    private Trainer baseTrainer(String firstName, String lastName, String email, String phone) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setEmail(email);
        trainer.setPhone(phone);
        trainer.setRole(UserRole.TRAINER);
        return trainer;
    }

    private static String blankToEmpty(String value) {
        return value != null ? value : "";
    }
}
