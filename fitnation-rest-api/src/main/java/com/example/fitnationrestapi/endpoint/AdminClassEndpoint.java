package com.example.fitnationrestapi.endpoint;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.ClassScheduleFilterRequest;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.dto.request.ScheduleClassRequest;
import com.example.fitnationcommon.dto.request.UpdateGroupClassRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationcommon.dto.response.GroupClassResponse;
import com.example.fitnationcommon.enums.ScheduleFilterStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Group classes", description = "Group class administration and class schedules")
public class AdminClassEndpoint {

    private final GroupClassService groupClassService;

    @Operation(summary = "Create group class", description = "ADMIN: create a new group class template.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Class created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/admin/classes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupClassResponse> createClass(@Valid @RequestBody CreateGroupClassRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupClassService.createClass(request));
    }

    @Operation(summary = "Update group class", description = "ADMIN: update class metadata.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Class updated"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    @PutMapping("/admin/classes/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GroupClassResponse updateClass(
            @PathVariable Long classId,
            @Valid @RequestBody UpdateGroupClassRequest request) {
        return groupClassService.updateClass(classId, request);
    }

    @Operation(summary = "Schedule class instance", description = "ADMIN: add a scheduled occurrence for a class.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Schedule created"),
            @ApiResponse(responseCode = "400", description = "Invalid schedule request")
    })
    @PostMapping("/admin/classes/{classId}/schedule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassScheduleItemResponse> scheduleClass(
            @PathVariable Long classId,
            @Valid @RequestBody ScheduleClassRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupClassService.scheduleClass(classId, request));
    }

    @Operation(summary = "Update schedule", description = "ADMIN: update an existing class schedule.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule updated"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    @PutMapping("/admin/classes/schedule/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClassScheduleItemResponse updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleClassRequest request) {
        return groupClassService.updateSchedule(scheduleId, request);
    }

    @Operation(summary = "Delete schedule", description = "ADMIN: remove a class schedule.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Schedule deleted"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    @DeleteMapping("/admin/classes/schedule/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        groupClassService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all schedules", description = "Returns published class schedules (authenticated).")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Schedules returned"))
    @GetMapping("/classes/schedule")
    public List<ClassScheduleItemResponse> getAllSchedules(
            @RequestParam(required = false) Long trainerId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) ScheduleFilterStatus status
    ) {
        return groupClassService.getAllSchedules(new ClassScheduleFilterRequest(trainerId, fromDate, toDate, status));
    }

    @Operation(summary = "List group classes", description = "ADMIN: list all group class templates.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Classes returned"))
    @GetMapping("/admin/group-classes")
    @PreAuthorize("hasRole('ADMIN')")
    public List<GroupClassResponse> listGroupClasses() {
        return groupClassService.listAllGroupClasses();
    }
}

