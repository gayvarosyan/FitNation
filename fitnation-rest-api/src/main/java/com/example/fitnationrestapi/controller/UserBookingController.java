package com.example.fitnationrestapi.controller;

import com.example.fitnationbooking.service.ClassBookingService;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENT', 'TRAINER', 'ADMIN')")
public class UserBookingController {

    private final ClassBookingService classBookingService;

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }
        throw new IllegalStateException("Unexpected principal type: " + principal);
    }

    @PostMapping("/classes/{scheduleId}/book")
    public ResponseEntity<Void> bookClass(@PathVariable Long scheduleId) {
        classBookingService.bookClass(scheduleId, currentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        classBookingService.cancelBooking(id, currentUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookings")
    public List<UserBookingItemResponse> getUserBookings() {
        return classBookingService.getUserBookings(currentUserId());
    }
}

