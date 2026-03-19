package com.example.fitnationbooking.service;

import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationbooking.entity.GroupClass;
import com.example.fitnationbooking.repository.ClassScheduleRepository;
import com.example.fitnationbooking.repository.GroupClassRepository;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.dto.request.ScheduleClassRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationtrainer.entity.Trainer;
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

    @Transactional
    public GroupClass createClass(CreateGroupClassRequest request) {
        Trainer trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + request.trainerId()));

        GroupClass groupClass = new GroupClass();
        groupClass.setName(request.name());
        groupClass.setDescription(request.description());
        groupClass.setCapacity(request.capacity());
        groupClass.setTrainer(trainer);
        return groupClassRepository.save(groupClass);
    }

    @Transactional
    public ClassSchedule scheduleClass(Long classId, ScheduleClassRequest request) {
        GroupClass groupClass = groupClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found: " + classId));

        ClassSchedule schedule = new ClassSchedule();
        schedule.setGroupClass(groupClass);
        schedule.setDate(request.date());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());

        return classScheduleRepository.save(schedule);
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
                .map(s -> new ClassScheduleItemResponse(
                        s.getId(),
                        s.getGroupClass().getName(),
                        s.getGroupClass().getTrainer().getFirstName() + " " + s.getGroupClass().getTrainer().getLastName(),
                        s.getDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getGroupClass().getCapacity()
                ))
                .toList();
    }
}