package com.example.fitnationrestapi.controller;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.dto.request.ScheduleClassRequest;
import com.example.fitnationcommon.dto.request.UpdateGroupClassRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationcommon.dto.response.GroupClassResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminClassController {

    private final GroupClassService groupClassService;

    @PostMapping("/admin/classes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupClassResponse> createClass(@Valid @RequestBody CreateGroupClassRequest request) {
        GroupClassResponse body = groupClassService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/admin/classes/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GroupClassResponse updateClass(
            @PathVariable Long classId,
            @Valid @RequestBody UpdateGroupClassRequest request) {
        return groupClassService.updateClass(classId, request);
    }

    @PostMapping("/admin/classes/{classId}/schedule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassScheduleItemResponse> scheduleClass(
            @PathVariable Long classId,
            @Valid @RequestBody ScheduleClassRequest request) {
        ClassScheduleItemResponse body = groupClassService.scheduleClass(classId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/admin/classes/schedule/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClassScheduleItemResponse updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleClassRequest request) {
        return groupClassService.updateSchedule(scheduleId, request);
    }

    @DeleteMapping("/admin/classes/schedule/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        groupClassService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/classes/schedule")
    public List<ClassScheduleItemResponse> getAllSchedules() {
        return groupClassService.getAllSchedules();
    }
}

