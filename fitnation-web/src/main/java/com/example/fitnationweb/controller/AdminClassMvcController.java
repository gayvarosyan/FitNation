package com.example.fitnationweb.controller;

import com.example.fitnationweb.service.MvcAdminClassService;
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

@Controller
@RequestMapping("/admin/classes")
@RequiredArgsConstructor
public class AdminClassMvcController {


    private final MvcAdminClassService mvcAdminClassService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String page(Model model) {
        mvcAdminClassService.populatePageModel(model);
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
        var result = mvcAdminClassService.createAndScheduleClass(name, description, capacity, trainerId, date, startTime, endTime);
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
        var result = mvcAdminClassService.updateClassAndSchedule(
                classId, scheduleId, name, description, capacity, trainerId, date, startTime, endTime);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }

    @PostMapping("/schedule/{scheduleId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSchedule(@PathVariable long scheduleId, RedirectAttributes redirectAttributes) {
        var result = mvcAdminClassService.deleteSchedule(scheduleId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}