package com.example.fitnationbooking.service;

import com.example.fitnationbooking.mapper.ClassBookingMapper;
import com.example.fitnationbooking.repository.ClassBookingRepository;
import com.example.fitnationbooking.repository.ClassScheduleRepository;
import com.example.fitnationbooking.validation.ClassBookingValidator;
import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.PageRequestParams;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationcommon.exception.ClassBookingNotFoundException;
import com.example.fitnationcommon.exception.ClassScheduleNotFoundException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationuser.validation.SoftDeleteValidationService;
import com.example.fitnationuser.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClassBookingService {

    private final ClassScheduleRepository classScheduleRepository;
    private final ClassBookingRepository classBookingRepository;
    private final UserRepository userRepository;
    private final ClassBookingMapper classBookingMapper;
    private final ClassBookingValidator classBookingValidator;
    private final SoftDeleteValidationService softDeleteValidationService;

    @Transactional
    public void bookClass(Long scheduleId, Long userId) {
        var schedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ClassScheduleNotFoundException(
                        ApplicationConstants.MSG_SCHEDULE_NOT_FOUND + scheduleId));

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        ApplicationConstants.MSG_USER_NOT_FOUND + userId));

        softDeleteValidationService.validateUserForBooking(user);
        classBookingValidator.validateCanBook(schedule, user);

        var booking = classBookingMapper.toBookedEntity(schedule, user);
        classBookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        ApplicationConstants.MSG_USER_NOT_FOUND + userId));

        softDeleteValidationService.validateUserForBooking(user);

        var booking = classBookingRepository.findByIdAndUser(bookingId, user)
                .orElseThrow(() -> new ClassBookingNotFoundException(
                        ApplicationConstants.MSG_BOOKING_NOT_FOUND + bookingId));

        booking.setStatus(ClassBookingStatus.CANCELLED);
    }

    @Transactional
    public PagedResponse<UserBookingItemResponse> getUserBookings(Long userId, Integer page, Integer size, String sort, String status) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        ApplicationConstants.MSG_USER_NOT_FOUND + userId));

        softDeleteValidationService.validateUserForBooking(user);

        Pageable pageable = PageRequestParams.toPageable(page, size, sort,
                Set.of("status", "createdAt"));

        Page<UserBookingItemResponse> bookingPage = classBookingRepository.findByUser(user, pageable)
                .map(classBookingMapper::toUserBookingItemResponse);

        return PagedResponse.of(bookingPage, sort);
    }
}