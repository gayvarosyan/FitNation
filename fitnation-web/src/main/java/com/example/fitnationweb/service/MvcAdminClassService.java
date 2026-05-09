package com.example.fitnationweb.service;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.dto.request.ScheduleClassRequest;
import com.example.fitnationcommon.dto.request.UpdateGroupClassRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MvcAdminClassService {

    private static final String CLASSES_PATH = "/admin/classes";

    private final GroupClassService groupClassService;
    private final TrainerManagementService trainerManagementService;

    public void populatePageModel(Model model) {
        List<ClassScheduleItemResponse> schedules = groupClassService.getAllSchedules(null);
        List<ClassScheduleView> rows = schedules.stream()
                .map(s -> new ClassScheduleView(s, toHm(s.startTime()), toHm(s.endTime())))
                .toList();

        model.addAttribute("scheduleRows", rows);
        model.addAttribute("trainerOptions", trainerManagementService.getDirectory(0, 100, null, null, null).getItems());
        model.addAttribute("navSection", "classes");
        model.addAttribute("classesThisWeek", countClassesThisWeek(schedules));
        model.addAttribute("totalSchedules", schedules.size());
        model.addAttribute("uniqueClassIds", countUniqueClasses(schedules));
    }

    public MvcRedirect createAndScheduleClass(
            String name,
            String description,
            int capacity,
            long trainerId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {
        try {
            var created = groupClassService.createClass(new CreateGroupClassRequest(
                    name,
                    description,
                    capacity,
                    trainerId
            ));
            groupClassService.scheduleClass(created.id(), new ScheduleClassRequest(date, startTime, endTime));
            return MvcRedirect.to(CLASSES_PATH, "Class created and scheduled successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(CLASSES_PATH, e.getMessage());
        }
    }

    public MvcRedirect updateClassAndSchedule(
            long classId,
            long scheduleId,
            String name,
            String description,
            int capacity,
            long trainerId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {
        try {
            groupClassService.updateClass(classId, new UpdateGroupClassRequest(
                    name,
                    description,
                    capacity,
                    trainerId
            ));
            groupClassService.updateSchedule(scheduleId, new ScheduleClassRequest(date, startTime, endTime));
            return MvcRedirect.to(CLASSES_PATH, "Class updated successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(CLASSES_PATH, e.getMessage());
        }
    }

    public MvcRedirect deleteSchedule(long scheduleId) {
        try {
            groupClassService.deleteSchedule(scheduleId);
            return MvcRedirect.to(CLASSES_PATH, "Schedule deleted.");
        } catch (Exception e) {
            return MvcRedirect.failure(CLASSES_PATH, e.getMessage());
        }
    }

    public record ClassScheduleView(ClassScheduleItemResponse item, String startHm, String endHm) {}

    private static String toHm(LocalTime t) {
        if (t == null) {
            return "";
        }
        String s = t.toString();
        return s.length() >= 5 ? s.substring(0, 5) : s;
    }

    private static long countClassesThisWeek(List<ClassScheduleItemResponse> list) {
        var weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        var weekEnd = weekStart.plusWeeks(1);
        return list.stream()
                .map(ClassScheduleItemResponse::date)
                .filter(Objects::nonNull)
                .filter(d -> !d.isBefore(weekStart) && d.isBefore(weekEnd))
                .count();
    }

    private static int countUniqueClasses(List<ClassScheduleItemResponse> list) {
        return (int) list.stream()
                .map(ClassScheduleItemResponse::classId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }
}

