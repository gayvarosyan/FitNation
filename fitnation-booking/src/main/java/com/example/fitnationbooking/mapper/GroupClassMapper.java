package com.example.fitnationbooking.mapper;

import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationbooking.entity.GroupClass;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.dto.request.ScheduleClassRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationcommon.dto.response.GroupClassResponse;
import com.example.fitnationtrainer.entity.Trainer;
import org.springframework.stereotype.Component;

@Component
public class GroupClassMapper {

    public GroupClass toEntity(CreateGroupClassRequest request, Trainer trainer) {
        var groupClass = new GroupClass();
        groupClass.setName(request.name());
        groupClass.setDescription(request.description());
        groupClass.setCapacity(request.capacity());
        groupClass.setTrainer(trainer);
        return groupClass;
    }

    public ClassSchedule toSchedule(GroupClass groupClass, ScheduleClassRequest request) {
        var schedule = new ClassSchedule();
        schedule.setGroupClass(groupClass);
        schedule.setDate(request.date());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        return schedule;
    }

    public GroupClassResponse toGroupClassResponse(GroupClass groupClass) {
        return new GroupClassResponse(
                groupClass.getId(),
                groupClass.getName(),
                groupClass.getDescription(),
                groupClass.getCapacity(),
                groupClass.getTrainer().getId()
        );
    }

    public ClassScheduleItemResponse toScheduleItemResponse(ClassSchedule schedule) {
        var groupClass = schedule.getGroupClass();
        var trainer = groupClass.getTrainer();
        var trainerName = trainer.getFirstName() + " " + trainer.getLastName();
        return new ClassScheduleItemResponse(
                schedule.getId(),
                groupClass.getName(),
                trainerName,
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                groupClass.getCapacity()
        );
    }
}
