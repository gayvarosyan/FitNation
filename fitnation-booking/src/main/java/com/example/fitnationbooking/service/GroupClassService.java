package com.example.fitnationbooking.service;

import com.example.fitnationbooking.mapper.GroupClassMapper;
import com.example.fitnationbooking.repository.ClassScheduleRepository;
import com.example.fitnationbooking.repository.GroupClassRepository;
import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.dto.request.ScheduleClassRequest;
import com.example.fitnationcommon.dto.request.UpdateGroupClassRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationcommon.dto.response.GroupClassResponse;
import com.example.fitnationcommon.exception.ClassScheduleNotFoundException;
import com.example.fitnationcommon.exception.GroupClassNotFoundException;
import com.example.fitnationcommon.exception.TrainerNotFoundException;
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
                .orElseThrow(() -> new TrainerNotFoundException(
                        ApplicationConstants.MSG_TRAINER_NOT_FOUND + request.trainerId()));

        var groupClass = groupClassMapper.toEntity(request, trainer);
        var saved = groupClassRepository.save(groupClass);
        return groupClassMapper.toGroupClassResponse(saved);
    }

    @Transactional
    public ClassScheduleItemResponse scheduleClass(Long classId, ScheduleClassRequest request) {
        var groupClass = groupClassRepository.findById(classId)
                .orElseThrow(() -> new GroupClassNotFoundException(
                        ApplicationConstants.MSG_CLASS_NOT_FOUND + classId));

        var schedule = groupClassMapper.toSchedule(groupClass, request);
        var saved = classScheduleRepository.save(schedule);
        var loaded = classScheduleRepository.findByIdWithClassAndTrainer(saved.getId())
                .orElseThrow(() -> new ClassScheduleNotFoundException(
                        ApplicationConstants.MSG_SCHEDULE_NOT_FOUND_AFTER_SAVE + saved.getId()));
        return groupClassMapper.toScheduleItemResponse(loaded);
    }

    @Transactional
    public GroupClassResponse updateClass(Long classId, UpdateGroupClassRequest request) {
        var groupClass = groupClassRepository.findById(classId)
                .orElseThrow(() -> new GroupClassNotFoundException(
                        ApplicationConstants.MSG_CLASS_NOT_FOUND + classId));

        var trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> new TrainerNotFoundException(
                        ApplicationConstants.MSG_TRAINER_NOT_FOUND + request.trainerId()));

        groupClassMapper.updateEntity(groupClass, request, trainer);

        var saved = groupClassRepository.save(groupClass);
        return groupClassMapper.toGroupClassResponse(saved);
    }

    @Transactional
    public ClassScheduleItemResponse updateSchedule(Long scheduleId, ScheduleClassRequest request) {
        var schedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ClassScheduleNotFoundException(
                        ApplicationConstants.MSG_SCHEDULE_NOT_FOUND + scheduleId));

        groupClassMapper.updateSchedule(schedule, request);

        var saved = classScheduleRepository.save(schedule);
        var loaded = classScheduleRepository.findByIdWithClassAndTrainer(saved.getId())
                .orElseThrow(() -> new ClassScheduleNotFoundException(
                        ApplicationConstants.MSG_SCHEDULE_NOT_FOUND_AFTER_UPDATE + saved.getId()));
        return groupClassMapper.toScheduleItemResponse(loaded);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        classScheduleRepository.deleteById(scheduleId);
    }

    @Transactional
    public List<ClassScheduleItemResponse> getAllSchedules() {
        return classScheduleRepository.findAllWithClassAndTrainer()
                .stream()
                .map(groupClassMapper::toScheduleItemResponse)
                .toList();
    }
}