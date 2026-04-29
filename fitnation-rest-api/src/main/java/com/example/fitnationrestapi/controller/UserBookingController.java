package com.example.fitnationrestapi.controller;

import com.example.fitnationbooking.service.ClassBookingService;
import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.ClassScheduleFilterRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.enums.ScheduleFilterStatus;
import com.example.fitnationuser.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENT', 'TRAINER', 'ADMIN')")
@Tag(name = "Bookings", description = "Class bookings for the authenticated user (CLIENT, TRAINER, or ADMIN)")
public class UserBookingController {

    private final ClassBookingService classBookingService;
    private final GroupClassService groupClassService;

    @Operation(
            summary = "List available classes (paged)",
            description = """
                Searchable, paginated list of available group class schedules.

                **q** searches (case-insensitive, partial match):
                • class name  
                • trainer firstName  
                • trainer lastName  

                **sort** allowed fields: date, startTime, createdAt  
                Example: `date,asc`

                **size** max: 100, default: 20  
                **page** 0-based, default: 0  

                **dateFrom / dateTo** optional ISO date range filter (yyyy-MM-dd)  
                **trainerId** optional filter by trainer
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination params (bad sort field, negative page, size > 100)")
    })
    @GetMapping("/classes")
    public List<ClassScheduleItemResponse> getAvailableClasses(
            @RequestParam(required = false) Long trainerId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) ScheduleFilterStatus status
    ) {
        return groupClassService.getAllSchedules(
                new ClassScheduleFilterRequest(trainerId, fromDate, toDate, status));
    }

    @Operation(summary = "Book a class", description = "Books the given schedule for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Cannot book (e.g. full or invalid schedule)")
    })
    @PostMapping("/classes/{scheduleId}/book")
    public ResponseEntity<Void> bookClass(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal User user) {
        classBookingService.bookClass(scheduleId, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Cancel booking")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking cancelled"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel booking"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        classBookingService.cancelBooking(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "List my bookings (paged)",
            description = """
                Paginated booking history for the authenticated user.

                **sort** allowed fields: date, status, createdAt  
                Example: `date,desc`

                **size** max: 100, default: 20  
                **page** 0-based, default: 0  

                **status** optional filter: BOOKED, CANCELLED, ATTENDED  

                **q** — not supported for this endpoint (ignored).
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bookings returned"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination params (bad sort field, negative page, size > 100)")
    })
    @GetMapping("/bookings")
    public PagedResponse<UserBookingItemResponse> getUserBookings(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String status) {
        return classBookingService.getUserBookings(user.getId(), page, size, sort, status);
    }
}