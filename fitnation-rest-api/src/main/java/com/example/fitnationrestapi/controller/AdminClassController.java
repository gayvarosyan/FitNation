package com.example.fitnationrestapi.controller;

import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationbooking.entity.GroupClass;
import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.dto.request.ScheduleClassRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminClassController {

    private final GroupClassService groupClassService;

    @PostMapping("/admin/classes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupClass> createClass(@Valid @RequestBody CreateGroupClassRequest request) {
        GroupClass groupClass = groupClassService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupClass);
    }

    @PostMapping("/admin/classes/{classId}/schedule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassSchedule> scheduleClass(
            @PathVariable Long classId,
            @Valid @RequestBody ScheduleClassRequest request) {
        ClassSchedule schedule = groupClassService.scheduleClass(classId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }

    @GetMapping("/classes/schedule")
    public List<ClassScheduleItemResponse> getAllSchedules() {
        return groupClassService.getAllSchedules();
    }
}

