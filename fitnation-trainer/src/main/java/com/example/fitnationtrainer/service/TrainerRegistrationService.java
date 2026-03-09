package com.example.fitnationtrainer.service;

import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationtrainer.entity.Trainer;

public interface TrainerRegistrationService {

    Trainer register(RegisterRequest request);
}
