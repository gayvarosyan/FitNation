package com.example.fitnationbooking.service;

import com.example.fitnationbooking.entity.GroupClass;
import com.example.fitnationbooking.mapper.ClassBookingMapper;
import com.example.fitnationbooking.repository.ClassBookingRepository;
import com.example.fitnationbooking.repository.ClassScheduleRepository;
import com.example.fitnationbooking.validation.ClassBookingValidator;
import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.PageRequestParams;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.dto.response.UserBookingItemResponse;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationcommon.exception.ClassBookingNotFoundException;
import com.example.fitnationcommon.exception.ClassScheduleNotFoundException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationprogress.factory.NotificationCommandFactory;
import com.example.fitnationprogress.service.NotificationCommandPublisher;
import com.example.fitnationuser.validation.SoftDeleteValidationService;
import com.example.fitnationuser.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClassBookingService {

    private final ClassScheduleRepository classScheduleRepository;
    private final ClassBookingRepository classBookingRepository;
    private final UserRepository userRepository;
    private final ClassBookingMapper classBookingMapper;
    private final ClassBookingValidator classBookingValidator;
    private final NotificationCommandPublisher notificationCommandPublisher;
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
        var saved = classBookingRepository.save(booking);
        var scheduleLoaded = classScheduleRepository.findByIdWithClassAndTrainer(scheduleId).orElse(schedule);
        var groupClass = scheduleLoaded.getGroupClass();
        var trainer = groupClass.getTrainer();
        var trainerDisplayName = formatPersonName(groupClass);
        var dateStr = scheduleLoaded.getDate().toString();
        var timeStr = scheduleLoaded.getStartTime().toString();
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.classBooked(
                        saved.getId(),
                        userId,
                        groupClass.getName(),
                        dateStr,
                        timeStr,
                        trainerDisplayName));
        var bookedCount = classBookingRepository.countBySchedule_IdAndStatus(
                scheduleId, ClassBookingStatus.BOOKED);
        var capacity = groupClass.getCapacity();
        if (capacity != null && bookedCount >= capacity) {
            notificationCommandPublisher.publishAfterCommit(
                    NotificationCommandFactory.classFull(
                            scheduleId,
                            trainer.getId(),
                            groupClass.getName(),
                            dateStr,
                            timeStr));
        }
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(ApplicationConstants.MSG_USER_NOT_FOUND + userId);
        }

        softDeleteValidationService.validateUserForBooking(user);

        var booking = classBookingRepository.findByIdAndUser(bookingId, user)
                .orElseThrow(() -> new ClassBookingNotFoundException(
                        ApplicationConstants.MSG_BOOKING_NOT_FOUND + bookingId));

        var schedule = booking.getSchedule();
        var groupClass = schedule.getGroupClass();
        var trainer = groupClass.getTrainer();
        notificationCommandPublisher.publishAfterCommit(
                NotificationCommandFactory.classCanceled(
                        booking.getId(),
                        userId,
                        trainer.getId(),
                        groupClass.getName(),
                        schedule.getDate().toString(),
                        schedule.getStartTime().toString()));
        booking.setStatus(ClassBookingStatus.CANCELLED);
    }

    @Transactional
    public PagedResponse<UserBookingItemResponse> getUserBookings(Long userId, Integer page, Integer size, String sort, String status) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        ApplicationConstants.MSG_USER_NOT_FOUND + userId));


        softDeleteValidationService.validateUserForBooking(user);

        var pageable = PageRequestParams.toPageable(page, size, sort,
                Set.of("status", "createdAt"));
        var bookingStatus = status != null ? ClassBookingStatus.valueOf(status.toUpperCase()) : null;

        var resultPage = bookingStatus != null
                ? classBookingRepository.findByUserAndStatus(user, bookingStatus, pageable)

                .map(classBookingMapper::toUserBookingItemResponse)
                : classBookingRepository.findByUser(user, pageable)
                .map(classBookingMapper::toUserBookingItemResponse);

        return PagedResponse.of(resultPage, sort);
    }

    private static String formatPersonName(GroupClass groupClass) {
        var trainer = groupClass.getTrainer();
        return trainer.getFirstName().trim() + " " + trainer.getLastName().trim();
    }
}