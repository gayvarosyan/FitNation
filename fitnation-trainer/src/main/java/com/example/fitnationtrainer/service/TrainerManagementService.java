package com.example.fitnationtrainer.service;

import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerStatsResponse;


public interface TrainerManagementService {

    TrainerStatsResponse getStats();

    PagedResponse<TrainerDirectoryItem> getDirectory(Integer page, Integer size, String sort, String q, String status);

    TrainerDirectoryItem create(CreateTrainerRequest request);

    TrainerDirectoryItem edit(Long id, EditTrainerRequest request);

    void delete(Long id);
}
