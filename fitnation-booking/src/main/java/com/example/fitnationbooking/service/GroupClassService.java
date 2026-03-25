package com.example.fitnationbooking.service;

import com.example.fitnationbooking.mapper.GroupClassMapper;
import com.example.fitnationbooking.repository.ClassScheduleRepository;
import com.example.fitnationbooking.repository.GroupClassRepository;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.dto.request.ScheduleClassRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationcommon.dto.response.GroupClassResponse;
import com.example.fitnationtrainer.repository.TrainerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupClassService {

    private final GroupClassRepository groupClassRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final TrainerRepository trainerRepository;
    private final GroupClassMapper groupClassMapper;

    @Transactional
    public GroupClassResponse createClass(CreateGroupClassRequest request) {
        var trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + request.trainerId()));

        var groupClass = groupClassMapper.toEntity(request, trainer);
        var saved = groupClassRepository.save(groupClass);
        return groupClassMapper.toGroupClassResponse(saved);
    }

    @Transactional
    public ClassScheduleItemResponse scheduleClass(Long classId, ScheduleClassRequest request) {
        var groupClass = groupClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found: " + classId));

        var schedule = groupClassMapper.toSchedule(groupClass, request);
        var saved = classScheduleRepository.save(schedule);
        var loaded = classScheduleRepository.findByIdWithClassAndTrainer(saved.getId())
                .orElseThrow(() -> new IllegalStateException("Schedule not found after save: " + saved.getId()));
        return groupClassMapper.toScheduleItemResponse(loaded);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        classScheduleRepository.deleteById(scheduleId);
    }

    @Transactional
    public void deleteClass(Long classId) {
        groupClassRepository.deleteById(classId);
    }

    @Transactional
    public List<ClassScheduleItemResponse> getAllSchedules() {
        return classScheduleRepository.findAllWithClassAndTrainer()
                .stream()
                .map(groupClassMapper::toScheduleItemResponse)
                .toList();
    }
}