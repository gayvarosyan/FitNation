package com.example.fitnationtrainer.service;

import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerStatsResponse;

import java.util.List;

public interface TrainerManagementService {

    TrainerStatsResponse getStats();

    List<TrainerDirectoryItem> getDirectory();

    TrainerDirectoryItem create(CreateTrainerRequest request);

    TrainerDirectoryItem edit(Long id, EditTrainerRequest request);

    void delete(Long id);
}
