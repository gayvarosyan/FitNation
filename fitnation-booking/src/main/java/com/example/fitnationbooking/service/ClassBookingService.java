package com.example.fitnationbooking.service;

import com.example.fitnationbooking.mapper.ClassBookingMapper;
import com.example.fitnationbooking.repository.ClassBookingRepository;
import com.example.fitnationbooking.repository.ClassScheduleRepository;
import com.example.fitnationbooking.validation.ClassBookingValidator;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationuser.repository.UserRepository;
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
    private final ClassBookingMapper classBookingMapper;
    private final ClassBookingValidator classBookingValidator;

    @Transactional
    public void bookClass(Long scheduleId, Long userId) {
        var schedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        classBookingValidator.validateCanBook(schedule, user);

        var booking = classBookingMapper.toBookedEntity(schedule, user);
        classBookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        var booking = classBookingRepository.findByIdAndUser(bookingId, user)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setStatus(ClassBookingStatus.CANCELLED);
    }

    @Transactional
    public List<UserBookingItemResponse> getUserBookings(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return classBookingRepository.findByUser(user).stream()
                .map(classBookingMapper::toUserBookingItemResponse)
                .toList();
    }
}