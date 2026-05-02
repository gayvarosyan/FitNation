package com.example.fitnationweb.endpoint;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.dto.request.ScheduleClassRequest;
import com.example.fitnationcommon.dto.request.UpdateGroupClassRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationweb.support.MvcRedirect;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin/classes")
@RequiredArgsConstructor
public class AdminClassMvcEndpoint {

    private static final String CLASSES_PATH = "/admin/classes";

    private final GroupClassService groupClassService;
    private final TrainerManagementService trainerManagementService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String page(Model model) {
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
        return "admin/classes";
    }

    @PostMapping("/schedule-new")
    @PreAuthorize("hasRole('ADMIN')")
    public String scheduleNew(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam int capacity,
            @RequestParam long trainerId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            RedirectAttributes redirectAttributes) {
        var result = createAndScheduleClass(name, description, capacity, trainerId, date, startTime, endTime);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(
            @RequestParam long classId,
            @RequestParam long scheduleId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam int capacity,
            @RequestParam long trainerId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            RedirectAttributes redirectAttributes) {
        var result = updateClassAndSchedule(classId, scheduleId, name, description, capacity, trainerId, date, startTime, endTime);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/schedule/{scheduleId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSchedule(@PathVariable long scheduleId, RedirectAttributes redirectAttributes) {
        var result = deleteScheduleInternal(scheduleId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    public record ClassScheduleView(ClassScheduleItemResponse item, String startHm, String endHm) {}

    private MvcRedirect createAndScheduleClass(
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
                    description != null ? description : null,
                    capacity,
                    trainerId
            ));
            groupClassService.scheduleClass(created.id(), new ScheduleClassRequest(date, startTime, endTime));
            return MvcRedirect.to(CLASSES_PATH, "Class created and scheduled successfully.");
        } catch (Exception e) {
            return MvcRedirect.failure(CLASSES_PATH, e.getMessage());
        }
    }

    private MvcRedirect updateClassAndSchedule(
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

    private MvcRedirect deleteScheduleInternal(long scheduleId) {
        try {
            groupClassService.deleteSchedule(scheduleId);
            return MvcRedirect.to(CLASSES_PATH, "Schedule deleted.");
        } catch (Exception e) {
            return MvcRedirect.failure(CLASSES_PATH, e.getMessage());
        }
    }

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