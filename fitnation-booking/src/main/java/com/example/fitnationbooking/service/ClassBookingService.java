package com.example.fitnationbooking.service;

import com.example.fitnationbooking.entity.ClassBooking;
import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationbooking.repository.ClassBookingRepository;
import com.example.fitnationbooking.repository.ClassScheduleRepository;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassBookingService {

    private final ClassScheduleRepository classScheduleRepository;
    private final ClassBookingRepository classBookingRepository;
    private final UserRepository userRepository;

    @Transactional
    public void bookClass(Long scheduleId, Long userId) {
        ClassSchedule schedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        long booked = classBookingRepository.countByScheduleAndStatus(schedule, ClassBookingStatus.BOOKED);
        Integer capacity = schedule.getGroupClass().getCapacity();
        if (capacity != null && booked >= capacity) {
            throw new IllegalStateException("Class is full");
        }

        if (classBookingRepository.existsByScheduleAndUserAndStatus(schedule, user, ClassBookingStatus.BOOKED)) {
            throw new IllegalStateException("You already booked this class");
        }

        ClassBooking booking = new ClassBooking();
        booking.setSchedule(schedule);
        booking.setUser(user);
        booking.setStatus(ClassBookingStatus.BOOKED);

        classBookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        ClassBooking booking = classBookingRepository.findByIdAndUser(bookingId, user)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setStatus(ClassBookingStatus.CANCELLED);
    }

    @Transactional
    public List<UserBookingItemResponse> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return classBookingRepository.findByUser(user).stream()
                .map(b -> new UserBookingItemResponse(
                        b.getId(),
                        b.getSchedule().getGroupClass().getName(),
                        b.getSchedule().getGroupClass().getTrainer().getFirstName() + " " +
                                b.getSchedule().getGroupClass().getTrainer().getLastName(),
                        b.getSchedule().getDate(),
                        b.getSchedule().getStartTime(),
                        b.getSchedule().getEndTime(),
                        b.getStatus()
                ))
                .toList();
    }
}